package com.sy.qfb.controller;

import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import android.os.AsyncTask;

import com.sy.qfb.exception.IException;

public class BaseController {

    @SuppressWarnings({"rawtypes" })
    private ConcurrentHashMap<String, AsyncTask> asyncTaskMap = new ConcurrentHashMap<String, AsyncTask>();

    protected <Param, Progress, Result> void doAsyncTask(
            final int taskKey,
            final UpdateViewAsyncCallback<Result> updateViewAsyncCallback,
            final DoAsyncTaskCallback<Param, Result> doAsyncTaskCallback,
            Param... params) {
        doAsyncTask(String.valueOf(taskKey), updateViewAsyncCallback, doAsyncTaskCallback, params);
    }


    protected <Param, Progress, Result> void doAsyncTask(
            final String taskKey,
            final UpdateViewAsyncCallback<Result> updateViewAsyncCallback,
            final DoAsyncTaskCallback<Param, Result> doAsyncTaskCallback,
            Param... params) {
        if (null == updateViewAsyncCallback || taskKey == null) {
            return;
        }

        AsyncTask<Param, Void, Result> asyncTask = new AsyncTask<Param, Void, Result>() {
            private IException ie = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                updateViewAsyncCallback.onPreExecute();
            }

            @Override
            protected Result doInBackground(Param... params) {
                Result result = null;
                try {
                    result = doAsyncTaskCallback.doAsyncTask(params);
                } catch (IException ie) {
                    this.ie = ie;
                }
                return result;
            }

            @Override
            protected void onPostExecute(Result result) {
                super.onPostExecute(result);
                if (null == ie) {
                    updateViewAsyncCallback.onPostExecute(result);
                } else {
                    updateViewAsyncCallback.onException(ie);
                    ie = null;
                }
                asyncTaskMap.remove(taskKey);
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                updateViewAsyncCallback.onCancelled();
            }

        };

        cancel(taskKey);
        asyncTaskMap.put(String.valueOf(taskKey), asyncTask);
        asyncTask.execute(params);
    }

    public void cancel(int asyncTaskKey) {
        cancel(String.valueOf(asyncTaskKey));
    }

    public void cancel(String asyncTaskKey) {
        if (asyncTaskMap.containsKey(asyncTaskKey)) {
            asyncTaskMap.get(asyncTaskKey).cancel(true);
            asyncTaskMap.remove(asyncTaskKey);
        }
    }

    @SuppressWarnings({"rawtypes" })
    public void cancelAllTasks() {
        Set<Entry<String, AsyncTask>> entrySet= asyncTaskMap.entrySet();
        Iterator<Entry<String, AsyncTask>> it = entrySet.iterator();
        while(it.hasNext())
        {
            Entry<String, AsyncTask> entry = it.next();
            AsyncTask task = entry.getValue();
            if(task != null)
            {
                task.cancel(true);
            }
            it.remove();
        }
    }

    public interface DoAsyncTaskCallback<Param, Result> {
        public abstract Result doAsyncTask(Param... params) throws IException;
    }

    public interface UpdateViewAsyncCallback<Result> {
        public abstract void onPreExecute();

        public abstract void onPostExecute(Result result);

        public abstract void onCancelled();

        public abstract void onException(IException ie);
    }

    public abstract static class CommonUpdateViewAsyncCallback<Result>
            implements UpdateViewAsyncCallback<Result> {
        @Override
        public void onPreExecute() {
        };

        @Override
        public void onCancelled() {
        };
    }
}
