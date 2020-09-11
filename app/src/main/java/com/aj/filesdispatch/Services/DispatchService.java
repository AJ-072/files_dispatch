package com.aj.filesdispatch.Services;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    private ReceivingFileTask fileTask;
    private static final String TAG = "DispatchService";
    private Thread staringThread;
    private asyncFileSender fileSender;
    private static UserInfo connectedDevice, myDevice;
    private static OnBindToService bindToService = null;
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
        helper = new DatabaseHelper(this, 1);
        connected = new NotificationCompat.Builder(this, ApplicationActivity.show)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle("Connected!")
                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .build();
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
                            helper.setIdValue(connectedDevice.getUserName() + "_" + Converter.GetDate(System.currentTimeMillis()));
                            receiveBuffer = connectedDevice.getByteReceiverSpeed();
                            bufferSize = Math.min(receiveBuffer, mySendBuffer);
                            fileTask = new ReceivingFileTask(input, this);
                            fileTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                if (fileSender == null || fileSender.getStatus() == AsyncTask.Status.FINISHED) {
                    fileSender = new asyncFileSender(objectOutputStream, items);
                    fileSender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else
                    fileSender.addShareItems(items);
            }
        }
    }

    public void removeItem(SentFileItem item) {
        if (item.getSender().equals(myDevice.getUserName())) {
            fileSender.removeShareItem(item);
        } else {
            List<SentFileItem> fileItems = new ArrayList<>();
            fileItems.add(item);
            sentMsg(new MsgData(fileItems, null, ACTION_REMOVE, 0), objectOutputStream);
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
                if ((fileSender == null || fileSender.getStatus() == AsyncTask.Status.FINISHED) && socket.isConnected()) {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(output);
                    objectOutputStream.writeUnshared(new MsgData(null, null, ACTION_STOP, 0));
                    objectOutputStream.flush();
                } else if (socket.isConnected() && fileSender.getStatus() == AsyncTask.Status.RUNNING) {
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
        List<SentFileItem> sentFileItems;
        int sign;
        if (action == ACTION_ADD) {
            sentFileItems = items;
            sign = 1;
            TransferFile.addAll(items);
        } else {
            sentFileItems = items;
            TransferFile.removeAll(items);
            sign = -1;
        }
        if (fileListener != null) {
            fileListener.onFileListChanged(TransferFile);
        }
        int finalSign = sign;
        new Thread(() -> {
            for (SentFileItem fileItem : sentFileItems) {
                total = (int) (total + (finalSign * fileItem.getFileSize()));
            }
            if (bindToService != null)
                bindToService.setTotalSize(total);

        }).start();
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

    private static class asyncFileSender extends AsyncTask<Void, Integer, Boolean> {
        private ObjectOutputStream sending;
        private List<FileItem> sendingFileItems;
        private List<SentFileItem> newItems;
        private List<SentFileItem> sendingViews;
        private BufferedInputStream fileInput;
        private Action action = null;
        int count = 0;

        public asyncFileSender(ObjectOutputStream outputStream, List<FileItem> fileItems) {
            this.sending = outputStream;
            sendingFileItems = new ArrayList<>();
            newItems = new ArrayList<>();
            sendingViews = new ArrayList<>();
            addShareItems(fileItems);
        }

        @Override
        protected void onPreExecute() {

            Log.d(TAG, "onPreExecute: ");

            Log.d(TAG, "onPreExecute: " + (newItems != null ? (newItems.size() + newItems.get(0).getFileName()) : 0));
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: ");
            MsgData data;
            try {
                Log.d(TAG, "doInBackground: create sending");
                //sending = new ObjectOutputStream(outputStream);
                if (sendingFileItems.size() != 0) {
                    data = new MsgData(newItems, null, ACTION_ADD, 0);
                    sending.writeUnshared(data);
                    sending.flush();
                    sending.reset();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (count < sendingFileItems.size()) {
                FileItem currentItem = sendingFileItems.get(count);
                currentSendingFile = sendingViews.get(count);
                int bytes;
                byte[] buffer = new byte[bufferSize];
                try {
                    fileInput = new BufferedInputStream(new FileInputStream(new File(currentItem.getFileUri())));
                } catch (FileNotFoundException e) {
                    closeFile(fileInput);
                    e.printStackTrace();
                }
                while (true) {
                    try {
                        newItems.clear();
                        action = null;
                        if ((bytes = fileInput.read(buffer)) == -1) {
                            closeFile(fileInput);
                            break;
                        }
                        data = new MsgData(newItems, buffer, action, bytes);
                        sending.writeUnshared(data);
                        sending.flush();
                        sending.reset();
                        publishProgress(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                        closeFile(fileInput);
                        break;
                    }

                }
                count++;
                helper.addItem(currentItem);
                currentSendingFile.setFileUri(currentItem.getFileUri());
            }
            try {
                sending.writeUnshared(null);
                sending.flush();
                sending.reset();
                Log.d(TAG, "doInBackground: send null");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d(TAG, "onProgressUpdate: ");
            currentSendingFile.setProgress(currentSendingFile.getProgress() + values[0]);
            if (fileListener != null) {
                fileListener.onFileProgress(TransferFile.indexOf(currentSendingFile));
            }
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

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            this.cancel(false);
            sendingViews.clear();
            sendingFileItems.clear();
            currentSendingFile = null;
            newItems.clear();
        }

        public void addShareItems(List<FileItem> shareItems) {
            if (newItems != null)
                newItems.clear();
            action = null;
            this.sendingFileItems.addAll(shareItems);
            for (FileItem item : shareItems) {
                Log.d(TAG, "addShareItems: " + item.getFileName());
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

    public static class ReceivingFileTask extends AsyncTask<Void, Integer, Void> {
        private InputStream inputStream;
        private List<SentFileItem> receivingFiles;
        private boolean pause = false;
        private Context context;
        private ObjectInputStream fileInput;

        ReceivingFileTask(InputStream input, Context context) {
            this.inputStream = input;
            Log.d(TAG, "ReceivingFileTask: ");
            WeakReference<Context> weakContext = new WeakReference<>(context);
            this.context = weakContext.get();
        }

        @Override
        protected void onPreExecute() {
            receivingFiles = new ArrayList<>();
            currentReceivingFile = null;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MsgData data;
            try {
                fileInput = new ObjectInputStream(inputStream);
                while (true) {
                    Log.d(TAG, "doInBackground: fileinput waiting");
                    data = (MsgData) fileInput.readUnshared();
                    setAction(data);
                    if (data.getAction() == ACTION_STOP) break;
                    for (SentFileItem item : receivingFiles) {
                        Log.d(TAG, "doInBackground: still in log");
                        currentReceivingFile = item;
                        int total = 0;
                        File file = getLocation(item.getFileType(), item.getFileName());
                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
                        while (total < item.getFileSize()) {
                            Log.d(TAG, "doInBackground: lopping");
                            data = (MsgData) fileInput.readUnshared();
                            if (data != null) {
                                setAction(data);
                                total = total + data.getLength();
                                bufferedOutputStream.write(data.getBytes(), 0, data.getLength());
                                bufferedOutputStream.flush();
                                publishProgress(data.getLength());
                            } else
                                Log.d(TAG, "doInBackground: is null");
                        }
                        currentReceivingFile.setFileUri(file.getPath());
                        helper.addItem(currentReceivingFile);
                        bufferedOutputStream.close();
                    }
                    currentReceivingFile = null;
                    receivingFiles.clear();
                    fileInput.readUnshared();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void setAction(MsgData inputPack) {
            if (inputPack != null && inputPack.getAction() != null)
                switch (inputPack.getAction()) {
                    case ACTION_ADD:
                        receivingFiles.addAll(inputPack.getFileList());
                        alterTransferFile(inputPack.getAction(), inputPack.getFileList());
                        break;
                    case ACTION_REMOVE:
                        receivingFiles.removeAll(inputPack.getFileList());
                        alterTransferFile(inputPack.getAction(), inputPack.getFileList());
                        break;
                    case ACTION_PAUSE:
                        pause = true;
                        break;
                    case ACTION_RESUME:
                        pause = false;
                        break;
                }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            currentReceivingFile.setProgress(currentReceivingFile.getProgress() + values[0]);
            if (fileListener != null)
                fileListener.onFileProgress(TransferFile.indexOf(currentReceivingFile));
            if (bindToService != null)
                bindToService.setTotalProgress((totalProgress = totalProgress + values[0]));

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private File getLocation(String Extension, String fileName) {
            int count = 1;
            StringBuilder uri = new StringBuilder();
            uri = uri.append(Environment.getExternalStorageDirectory().getPath()).append("//")
                    .append(context.getString(R.string.app_name))
                    .append("//").append(Extension);
            if (Extension.equals(APPLICATION))
                uri.append("//").append(fileName).append(".apk");
            else
                uri.append("//").append(fileName);
            File file = new File(uri.toString());
            while (file.exists()) {
                file = new File(uri.insert(uri.lastIndexOf("."),count+".").toString());
                count++;
            }

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

