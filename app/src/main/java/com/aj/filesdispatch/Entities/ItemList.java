package com.aj.filesdispatch.Entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class ItemList extends ArrayList<FileItem> {
    List<FileItem> fileItemList = new ArrayList<>();

    public ItemList() {
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return super.containsAll(c);
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return super.contains(o);
    }

    @Override
    public int indexOf(@Nullable Object o) {
        return super.indexOf(o);
    }

    @Override
    public FileItem set(int index, FileItem element) {
        return super.set(index, element);
    }

    @Override
    public boolean add(FileItem item) {
        return super.add(item);
    }

    @Override
    public void add(int index, FileItem element) {
        super.add(index, element);
    }

    @Override
    public boolean remove(@Nullable Object o) {
        return super.remove(o);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends FileItem> c) {
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends FileItem> c) {
        return super.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return super.removeAll(c);
    }
}
