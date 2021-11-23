/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.iposprinter.iposprinterservice;
public interface IPosPrinterCallback extends android.os.IInterface
{
  /** Default implementation for IPosPrinterCallback. */
  public static class Default implements com.iposprinter.iposprinterservice.IPosPrinterCallback
  {
    @Override public void onRunResult(boolean isSuccess) throws android.os.RemoteException
    {
    }
    @Override public void onReturnString(java.lang.String result) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.iposprinter.iposprinterservice.IPosPrinterCallback
  {
    private static final java.lang.String DESCRIPTOR = "com.iposprinter.iposprinterservice.IPosPrinterCallback";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.iposprinter.iposprinterservice.IPosPrinterCallback interface,
     * generating a proxy if needed.
     */
    public static com.iposprinter.iposprinterservice.IPosPrinterCallback asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.iposprinter.iposprinterservice.IPosPrinterCallback))) {
        return ((com.iposprinter.iposprinterservice.IPosPrinterCallback)iin);
      }
      return new com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      switch (code)
      {
        case INTERFACE_TRANSACTION:
        {
          reply.writeString(descriptor);
          return true;
        }
        case TRANSACTION_onRunResult:
        {
          data.enforceInterface(descriptor);
          boolean _arg0;
          _arg0 = (0!=data.readInt());
          this.onRunResult(_arg0);
          return true;
        }
        case TRANSACTION_onReturnString:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          this.onReturnString(_arg0);
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements com.iposprinter.iposprinterservice.IPosPrinterCallback
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public java.lang.String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      @Override public void onRunResult(boolean isSuccess) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(((isSuccess)?(1):(0)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_onRunResult, _data, null, android.os.IBinder.FLAG_ONEWAY);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onRunResult(isSuccess);
            return;
          }
        }
        finally {
          _data.recycle();
        }
      }
      @Override public void onReturnString(java.lang.String result) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(result);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onReturnString, _data, null, android.os.IBinder.FLAG_ONEWAY);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onReturnString(result);
            return;
          }
        }
        finally {
          _data.recycle();
        }
      }
      public static com.iposprinter.iposprinterservice.IPosPrinterCallback sDefaultImpl;
    }
    static final int TRANSACTION_onRunResult = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_onReturnString = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    public static boolean setDefaultImpl(com.iposprinter.iposprinterservice.IPosPrinterCallback impl) {
      // Only one user of this interface can use this function
      // at a time. This is a heuristic to detect if two different
      // users in the same process use this function.
      if (Stub.Proxy.sDefaultImpl != null) {
        throw new IllegalStateException("setDefaultImpl() called twice");
      }
      if (impl != null) {
        Stub.Proxy.sDefaultImpl = impl;
        return true;
      }
      return false;
    }
    public static com.iposprinter.iposprinterservice.IPosPrinterCallback getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  public void onRunResult(boolean isSuccess) throws android.os.RemoteException;
  public void onReturnString(java.lang.String result) throws android.os.RemoteException;
}
