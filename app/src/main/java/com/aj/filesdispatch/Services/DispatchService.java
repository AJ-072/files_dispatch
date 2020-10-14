package com.aj.filesdispatch.Services;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.aj.filesdispatch.Activities.FileSendingProgress;
import com.aj.filesdispatch.ApplicationActivity;
import com.aj.filesdispatch.DatabaseHelper.DatabaseHelper;
import com.aj.filesdispatch.Entities.FileItem;
import com.aj.filesdispatch.Entities.MsgData;
import com.aj.filesdispatch.Entities.SentFileItem;
import com.aj.filesdispatch.Entities.UserInfo;
import com.aj.filesdispatch.Enums.Action;
import com.aj.filesdispatch.Interface.OnBindToService;
import com.aj.filesdispatch.Interface.SendingFIleListener;
import com.aj.filesdispatch.R;
import com.aj.filesdispatch.common.Converter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.aj.filesdispatch.Activities.FindConnection.IP_ADDRESS;
import static com.aj.filesdispatch.Activities.FindConnection.PORT;
import static com.aj.filesdispatch.ApplicationActivity.FILE_TO_SEND;
import static com.aj.filesdispatch.Enums.Action.ACTION_ADD;
import static com.aj.filesdispatch.Enums.Action.ACTION_REMOVE;
import static com.aj.filesdispatch.Enums.Action.ACTION_STOP;
import static com.aj.filesdispatch.Repository.AppListRepository.APPLICATION;

enum Me {CLIENT, SERVER}

public class DispatchService extends Service {
    public static final int NOTIFICATION_ID = 102;
    private Notification connected;
    private static Socket socket = null;
    private Me me;
    private static List<SentFileItem> TransferFile = new ArrayList<>();
    private static SentFileItem currentSendingFile, currentReceivingFile;
    private InputStream input;
    private OutputStream output;
    private ObjectOutputStream objectOutputStream;
    private FileReceiver fileReceiver;
    private static final String TAG = "DispatchService";
    private Thread staringThread;
    private FileSender fileSender;
    public static String APP_NAME;
    private static UserInfo connectedDevice, myDevice;
    private static OnBindToService bindToService = null;
    private ExecutorService fileSenderThread, fileReceiverThread;
    private static SendingFIleListener fileListener = null;
    private static int total = 0, totalProgress = 0, receiveBuffer, mySendBuffer, bufferSize;
    private static DatabaseHelper helper;

    public DispatchService() {
        super();
    }

    public List<SentFileItem> getReceivedFile() {
        return TransferFile;
    }

    public class DispatchBinder extends Binder {
        public DispatchService getServices() {
            return DispatchService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fileSenderThread = Executors.newSingleThreadExecutor();
        fileReceiverThread = Executors.newSingleThreadExecutor();
        helper = new DatabaseHelper(this, 1);
        connected = new NotificationCompat.Builder(this, ApplicationActivity.show)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle("Connected!")
                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .build();
        APP_NAME = this.getString(R.string.app_name);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, connected);
        if (socket == null) {
            List<FileItem> fileToSend = intent.getParcelableArrayListExtra(FILE_TO_SEND);
            String ip = intent.getStringExtra(IP_ADDRESS);
            int port = intent.getIntExtra(PORT, new Random().nextInt(10000));
            Log.d(TAG, "onStartCommand: " + ip + " " + port);
            staringThread = new Thread(() -> {
                if (ip == null) {
                    Log.d(TAG, "onStartCommand: server");
                    try {
                        ServerSocket server = new ServerSocket();
                        server.setReuseAddress(true);
                        server.bind(new InetSocketAddress(port));
                        socket = server.accept();
                        me = Me.SERVER;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d(TAG, "onStartCommand: client");
                    try {
                        socket = new Socket();
                        socket.bind(null);
                        socket.connect(new InetSocketAddress(ip, port), 600000);
                        me = Me.CLIENT;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    while (true)
                        if (socket != null && socket.isConnected()) {
                            Log.d(TAG, "onStartCommand: connected");
                            input = socket.getInputStream();
                            output = socket.getOutputStream();
                            socket.setKeepAlive(true);
                            mySendBuffer = socket.getSendBufferSize();
                            myDevice = new UserInfo(socket.getReceiveBufferSize());
                            new ObjectOutputStream(output).writeUnshared(myDevice);
                            connectedDevice = (UserInfo) new ObjectInputStream(input).readUnshared();
                            helper.setIdValue(connectedDevice.getUserName() + "_" + Converter.GetDate(0));
                            receiveBuffer = connectedDevice.getByteReceiverSpeed();
                            bufferSize = Math.min(receiveBuffer, mySendBuffer);
                            fileReceiver = new FileReceiver(input);
                            fileReceiverThread.submit(fileReceiver);
                            objectOutputStream = new ObjectOutputStream(output);
                            if (fileToSend != null && fileToSend.size() > 0)
                                setTransferFile(fileToSend);
                            break;
                        }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
            staringThread.start();
        }
        return START_NOT_STICKY;
    }

    public void setBindActivity(Activity activity) {
        bindToService = (OnBindToService) activity;
        if (bindToService != null)
            bindToService.getConnectedDeviceInfo(connectedDevice);
        if (activity instanceof FileSendingProgress)
            fileListener = (SendingFIleListener) activity;
        else
            fileListener = null;
    }

    public void setTransferFile(List<FileItem> items) {
        if (items != null && items.size() > 0) {
            if (socket != null && socket.isConnected()) {
                if (fileSender == null) {
                    fileSender = FileSender.getInstance(objectOutputStream, items);
                    fileSenderThread.submit(fileSender);
                    Log.d(TAG, "setTransferFile: file sender is null");
                } else {
                    Log.d(TAG, "setTransferFile: file sender is not null");
                    fileSender.addShareItems(items);
                }
            }
        }
    }

    public void changeAction(SentFileItem item) {
        if (item.getSender().equals(myDevice.getUserName())) {
            fileSender.removeShareItem(item);
        } else {
            List<SentFileItem> fileItems = new ArrayList<>();
            fileItems.add(item);
            sentMsg(new MsgData(fileItems, null, item.getWhat(), 0), objectOutputStream);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new DispatchBinder();
    }

    @Override
    public void onDestroy() {
        if (socket != null) {
            try {
                if (me == Me.SERVER) {
                    stopSocket();
                } else
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (staringThread.isAlive()) {
                staringThread.stop();
            }
        }
        super.onDestroy();
    }

    protected void stopSocket() {
        new Thread(() -> {
            try {
                if ((fileSender == null)) {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(output);
                    objectOutputStream.writeUnshared(new MsgData(null, null, ACTION_STOP, 0));
                    objectOutputStream.flush();
                } else if (socket.isConnected()) {
                    fileSender.setAction(ACTION_STOP);
                }
                Thread.sleep(3000);
                socket.close();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }).start();
    }

    protected static void alterTransferFile(Action action, List<SentFileItem> items) {
        List<SentFileItem> sentFileItems = new ArrayList<>();
        int sign = +1;
        switch (action) {
            case ACTION_PAUSE:
            case ACTION_RESUME:
                changeAction(action, items, TransferFile);
                break;
            case ACTION_REMOVE:
                changeAction(action, items, TransferFile);
                sentFileItems = items;
                sign = -1;
                break;
            case ACTION_ADD:
                sentFileItems = items;
                sign = 1;
                TransferFile.addAll(items);
                break;
        }
        if (fileListener != null) {
            fileListener.onFileListChanged(TransferFile);
        }
        int finalSign = sign;
        List<SentFileItem> finalSentFileItems = sentFileItems;
        new Thread(() -> {
            for (SentFileItem fileItem : finalSentFileItems) {
                total = (int) (total + (finalSign * fileItem.getFileSize()));
            }
            if (bindToService != null)
                bindToService.setTotalSize(total);

        }).start();
    }

    public static void changeAction(Action action, List<SentFileItem> from, List<SentFileItem> to) {
        for (SentFileItem item : from) {
            for (SentFileItem item1 : to) {
                if (item.getFileName().equals(item1.getFileName()) && item.getTime() == item1.getTime()) {
                    item1.setWhat(action);
                }
            }
        }
    }

    private void sentMsg(MsgData data, ObjectOutputStream objectOutputStream) {
        new Thread(() -> {
            try {
                objectOutputStream.writeUnshared(data);
                objectOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static class FileSender implements Runnable {
        private ObjectOutputStream sending;
        private volatile List<FileItem> sendingFileItems;
        private ArrayList<SentFileItem> newItems, itemsToSend;
        private volatile ArrayList<SentFileItem> sendingViews;
        private BufferedInputStream fileInput;
        private Action action = null, itemAction;
        private static FileSender fileSender = null;

        public FileSender(ObjectOutputStream outputStream, List<FileItem> fileItems) {
            this.sending = outputStream;
            sendingFileItems = new ArrayList<>();
            newItems = new ArrayList<>();
            itemsToSend = new ArrayList<>();
            sendingViews = new ArrayList<>();
            addShareItems(fileItems);
        }

        public static FileSender getInstance(ObjectOutputStream outputStream, List<FileItem> fileItems) {
            if (fileSender == null) {
                synchronized (FileSender.class) {
                    if (fileSender == null)
                        fileSender = new FileSender(outputStream, fileItems);
                    else
                        fileSender.addShareItems(fileItems);
                }
            }else
                fileSender.addShareItems(fileItems);
            return fileSender;
        }

        @Override
        public void run() {
            MsgData data;
            try {
                Log.d(TAG, "doInBackground: create sending");
                itemsToSend = (ArrayList<SentFileItem>) newItems.clone();
                if (itemsToSend.size() != 0) {
                    data = new MsgData(itemsToSend, null, ACTION_ADD, 0);
                    sending.writeUnshared(data);
                    sending.flush();
                    sending.reset();
                    newItems.clear();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (SentFileItem item : sendingViews) {
                currentSendingFile = item;
                int bytes, total = 0, i = 0;
                byte[] buffer = new byte[bufferSize];
                try {
                    fileInput = new BufferedInputStream(new FileInputStream(new File(item.getFileUri())));
                } catch (FileNotFoundException e) {
                    closeFile(fileInput);
                    e.printStackTrace();
                }
                if (currentSendingFile.getWhat() == ACTION_REMOVE)
                    continue;
                while (true) {
                    try {
                        if ((bytes = fileInput.read(buffer)) == -1) {
                            closeFile(fileInput);
                            break;
                        }
                        if (action != null) {
                            itemsToSend = (ArrayList<SentFileItem>) newItems.clone();
                            itemAction = action;
                            action = null;
                            newItems.clear();
                        }
                        data = new MsgData(itemsToSend, buffer, itemAction, bytes);
                        data.count = i;
                        sending.writeUnshared(data);
                        sending.flush();
                        sending.reset();
                        onProgressUpdate(bytes);
                        itemsToSend.clear();
                        itemAction = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                        closeFile(fileInput);
                        break;
                    }
                    total = total + bytes;
                    Log.d(TAG, "run: " + i + " " + item.getFileSize() + " " + total + " " + bytes);
                    i++;
                }
                helper.addItem(item);
            }
            try {
                sending.writeUnshared(null);
                sending.flush();
                sending.reset();
                Log.d(TAG, "doInBackground: send null");
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendingViews.clear();
            sendingFileItems.clear();
            currentSendingFile = null;
            newItems.clear();
        }

        private void onProgressUpdate(Integer... values) {
            currentSendingFile.setProgress(currentSendingFile.getProgress() + values[0]);
            if (fileListener != null)
                fileListener.onFileProgress(TransferFile.indexOf(currentSendingFile));
            if (bindToService != null)
                bindToService.setTotalProgress((totalProgress = totalProgress + values[0]));
        }

        protected void closeFile(BufferedInputStream fileInput) {
            try {
                fileInput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void setAction(Action action) {
            this.action = action;
        }

        public void addShareItems(List<FileItem> shareItems) {
            newItems.clear();
            action = null;
            for (FileItem item : shareItems) {
                newItems.add(new SentFileItem(item));
            }
            sendingViews.addAll(newItems);
            Log.d(TAG, "addShareItems: add");
            action = ACTION_ADD;
            alterTransferFile(action, newItems);
        }

        public void removeShareItem(SentFileItem fileItem) {
            newItems.add(fileItem);
            action = ACTION_REMOVE;
            sendingFileItems.remove(sendingViews.indexOf(fileItem));
            sendingViews.remove(fileItem);
            alterTransferFile(action, newItems);
        }

    }

    public static class FileReceiver implements Runnable {
        private InputStream inputStream;
        private volatile List<SentFileItem> receivingFiles;
        private boolean pause = false;

        public FileReceiver(InputStream input) {
            this.inputStream = input;
            receivingFiles = new ArrayList<>();
            currentReceivingFile = null;
        }

        @Override
        public void run() {
            MsgData data;
            try {
                ObjectInputStream fileInput = new ObjectInputStream(inputStream);
                while (true) {
                    Log.d(TAG, "doInBackground: fileinput waiting");
                    data = (MsgData) fileInput.readUnshared();
                    setAction(data);
                    Log.d(TAG, "run: " + data.count);
                    if (data.getAction() == ACTION_STOP) break;
                    for (SentFileItem item : receivingFiles) {
                        currentReceivingFile = item;
                        if (item.getWhat() == ACTION_REMOVE)
                            continue;
                        int total = 0;
                        File file = getLocation(item.getFileType(), item.getFileName());
                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
                        Log.d(TAG, "run: size" + item.getFileSize());
                        while (total < item.getFileSize()) {
                            data = (MsgData) fileInput.readUnshared();
                            if (data != null) {
                                setAction(data);
                                bufferedOutputStream.write(data.getBytes(), 0, data.getLength());
                                bufferedOutputStream.flush();
                                total = total + data.getLength();
                                onProgressUpdate(data.getLength());
                                Log.d(TAG, "run: size left " + data.count + " " + total + " " + data.getLength());
                            } else
                                Log.d(TAG, "run: data null");
                        }
                        currentReceivingFile.setFileUri(file.getPath());
                        onProgressUpdate(0);
                        helper.addItem(currentReceivingFile);
                        bufferedOutputStream.close();
                    }
                    currentReceivingFile = null;
                    receivingFiles.clear();
                    fileInput.readUnshared();
                }
                inputStream.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void setAction(MsgData inputPack) {
            if (inputPack != null && inputPack.getAction() != null)
                switch (inputPack.getAction()) {
                    case ACTION_ADD:
                        receivingFiles.addAll(inputPack.getFileList());
                        alterTransferFile(inputPack.getAction(), inputPack.getFileList());
                        break;
                    case ACTION_REMOVE:
                        Log.d(TAG, "setAction: " + receivingFiles.containsAll(inputPack.getFileList()));
                        DispatchService.changeAction(inputPack.getAction(), inputPack.getFileList(), receivingFiles);
                        alterTransferFile(inputPack.getAction(), inputPack.getFileList());
                        break;
                    case ACTION_PAUSE:
                        DispatchService.changeAction(inputPack.getAction(), inputPack.getFileList(), receivingFiles);
                        pause = true;
                        break;
                    case ACTION_RESUME:
                        DispatchService.changeAction(inputPack.getAction(), inputPack.getFileList(), receivingFiles);
                        pause = false;
                        break;
                }
        }

        protected void onProgressUpdate(Integer... values) {
            currentReceivingFile.setProgress(currentReceivingFile.getProgress() + values[0]);
            if (fileListener != null)
                fileListener.onFileProgress(TransferFile.indexOf(currentReceivingFile));
            if (bindToService != null)
                bindToService.setTotalProgress((totalProgress = totalProgress + values[0]));

        }

        private File getLocation(String Extension, String fileName) {
            int count = 0;
            File file;
            StringBuilder uri = new StringBuilder();
            uri = uri.append(Environment.getExternalStorageDirectory().getPath()).append("//")
                    .append(APP_NAME)
                    .append("//")
                    .append(Extension);
            new File(String.valueOf(uri)).mkdirs();
            if (Extension.equals(APPLICATION))
                uri.append("//").append(fileName).append(".apk");
            else
                uri.append("//").append(fileName);
            do {
                if (count == 0)
                    file = new File(uri.toString());
                else
                    file = new File(uri.insert(uri.lastIndexOf("."), "(" + count + ")").toString());
                count++;
            } while (file.exists());
            Log.d(TAG, "getLocation: " + file.getPath());
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file;
        }
    }
}

