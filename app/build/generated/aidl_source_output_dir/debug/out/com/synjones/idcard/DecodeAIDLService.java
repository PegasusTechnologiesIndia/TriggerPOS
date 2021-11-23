/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.synjones.idcard;
public interface DecodeAIDLService extends android.os.IInterface
{
  /** Default implementation for DecodeAIDLService. */
  public static class Default implements com.synjones.idcard.DecodeAIDLService
  {
    @Override public byte[] decode(byte[] wlt) throws android.os.RemoteException
    {
      return null;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.synjones.idcard.DecodeAIDLService
  {
    private static final java.lang.String DESCRIPTOR = "com.synjones.idcard.DecodeAIDLService";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.synjones.idcard.DecodeAIDLService interface,
     * generating a proxy if needed.
     */
    public static com.synjones.idcard.DecodeAIDLService asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.synjones.idcard.DecodeAIDLService))) {
        return ((com.synjones.idcard.DecodeAIDLService)iin);
      }
      return new com.synjones.idcard.DecodeAIDLService.Stub.Proxy(obj);
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
        case TRANSACTION_decode:
        {
          data.enforceInterface(descriptor);
          byte[] _arg0;
          _arg0 = data.createByteArray();
          byte[] _result = this.decode(_arg0);
          reply.writeNoException();
          reply.writeByteArray(_result);
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements com.synjones.idcard.DecodeAIDLService
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
      @Override public byte[] decode(byte[] wlt) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        byte[] _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeByteArray(wlt);
          boolean _status = mRemote.transact(Stub.TRANSACTION_decode, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().decode(wlt);
          }
          _reply.readException();
          _result = _reply.createByteArray();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      public static com.synjones.idcard.DecodeAIDLService sDefaultImpl;
    }
    static final int TRANSACTION_decode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    public static boolean setDefaultImpl(com.synjones.idcard.DecodeAIDLService impl) {
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
    public static com.synjones.idcard.DecodeAIDLService getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  public byte[] decode(byte[] wlt) throws android.os.RemoteException;
}
