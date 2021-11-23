/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.iposprinter.iposprinterservice;
public interface IPosPrinterService extends android.os.IInterface
{
  /** Default implementation for IPosPrinterService. */
  public static class Default implements com.iposprinter.iposprinterservice.IPosPrinterService
  {
    @Override public int getPrinterStatus() throws android.os.RemoteException
    {
      return 0;
    }
    @Override public void printerInit(com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
    {
    }
    @Override public void setPrinterPrintDepth(int depth, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
    {
    }
    @Override public void setPrinterPrintFontType(java.lang.String typeface, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
    {
    }
    @Override public void setPrinterPrintFontSize(int fontsize, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
    {
    }
    @Override public void setPrinterPrintAlignment(int alignment, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
    {
    }
    @Override public void printerFeedLines(int lines, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
    {
    }
    @Override public void printBlankLines(int lines, int height, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
    {
    }
    @Override public void printText(java.lang.String text, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
    {
    }
    @Override public void printSpecifiedTypeText(java.lang.String text, java.lang.String typeface, int fontsize, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
    {
    }
    @Override public void PrintSpecFormatText(java.lang.String text, java.lang.String typeface, int fontsize, int alignment, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
    {
    }
    @Override public void printColumnsText(java.lang.String[] colsTextArr, int[] colsWidthArr, int[] colsAlign, int isContinuousPrint, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
    {
    }
    @Override public void printBitmap(int alignment, int bitmapSize, android.graphics.Bitmap mBitmap, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
    {
    }
    @Override public void printBarCode(java.lang.String data, int symbology, int height, int width, int textposition, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
    {
    }
    @Override public void printQRCode(java.lang.String data, int modulesize, int mErrorCorrectionLevel, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
    {
    }
    @Override public void printRawData(byte[] rawPrintData, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
    {
    }
    @Override public void sendUserCMDData(byte[] data, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
    {
    }
    @Override public void printerPerformPrint(int feedlines, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.iposprinter.iposprinterservice.IPosPrinterService
  {
    private static final java.lang.String DESCRIPTOR = "com.iposprinter.iposprinterservice.IPosPrinterService";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.iposprinter.iposprinterservice.IPosPrinterService interface,
     * generating a proxy if needed.
     */
    public static com.iposprinter.iposprinterservice.IPosPrinterService asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.iposprinter.iposprinterservice.IPosPrinterService))) {
        return ((com.iposprinter.iposprinterservice.IPosPrinterService)iin);
      }
      return new com.iposprinter.iposprinterservice.IPosPrinterService.Stub.Proxy(obj);
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
        case TRANSACTION_getPrinterStatus:
        {
          data.enforceInterface(descriptor);
          int _result = this.getPrinterStatus();
          reply.writeNoException();
          reply.writeInt(_result);
          return true;
        }
        case TRANSACTION_printerInit:
        {
          data.enforceInterface(descriptor);
          com.iposprinter.iposprinterservice.IPosPrinterCallback _arg0;
          _arg0 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
          this.printerInit(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_setPrinterPrintDepth:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          com.iposprinter.iposprinterservice.IPosPrinterCallback _arg1;
          _arg1 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
          this.setPrinterPrintDepth(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_setPrinterPrintFontType:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          com.iposprinter.iposprinterservice.IPosPrinterCallback _arg1;
          _arg1 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
          this.setPrinterPrintFontType(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_setPrinterPrintFontSize:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          com.iposprinter.iposprinterservice.IPosPrinterCallback _arg1;
          _arg1 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
          this.setPrinterPrintFontSize(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_setPrinterPrintAlignment:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          com.iposprinter.iposprinterservice.IPosPrinterCallback _arg1;
          _arg1 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
          this.setPrinterPrintAlignment(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_printerFeedLines:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          com.iposprinter.iposprinterservice.IPosPrinterCallback _arg1;
          _arg1 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
          this.printerFeedLines(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_printBlankLines:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          int _arg1;
          _arg1 = data.readInt();
          com.iposprinter.iposprinterservice.IPosPrinterCallback _arg2;
          _arg2 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
          this.printBlankLines(_arg0, _arg1, _arg2);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_printText:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          com.iposprinter.iposprinterservice.IPosPrinterCallback _arg1;
          _arg1 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
          this.printText(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_printSpecifiedTypeText:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          int _arg2;
          _arg2 = data.readInt();
          com.iposprinter.iposprinterservice.IPosPrinterCallback _arg3;
          _arg3 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
          this.printSpecifiedTypeText(_arg0, _arg1, _arg2, _arg3);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_PrintSpecFormatText:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          int _arg2;
          _arg2 = data.readInt();
          int _arg3;
          _arg3 = data.readInt();
          com.iposprinter.iposprinterservice.IPosPrinterCallback _arg4;
          _arg4 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
          this.PrintSpecFormatText(_arg0, _arg1, _arg2, _arg3, _arg4);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_printColumnsText:
        {
          data.enforceInterface(descriptor);
          java.lang.String[] _arg0;
          _arg0 = data.createStringArray();
          int[] _arg1;
          _arg1 = data.createIntArray();
          int[] _arg2;
          _arg2 = data.createIntArray();
          int _arg3;
          _arg3 = data.readInt();
          com.iposprinter.iposprinterservice.IPosPrinterCallback _arg4;
          _arg4 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
          this.printColumnsText(_arg0, _arg1, _arg2, _arg3, _arg4);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_printBitmap:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          int _arg1;
          _arg1 = data.readInt();
          android.graphics.Bitmap _arg2;
          if ((0!=data.readInt())) {
            _arg2 = android.graphics.Bitmap.CREATOR.createFromParcel(data);
          }
          else {
            _arg2 = null;
          }
          com.iposprinter.iposprinterservice.IPosPrinterCallback _arg3;
          _arg3 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
          this.printBitmap(_arg0, _arg1, _arg2, _arg3);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_printBarCode:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          int _arg1;
          _arg1 = data.readInt();
          int _arg2;
          _arg2 = data.readInt();
          int _arg3;
          _arg3 = data.readInt();
          int _arg4;
          _arg4 = data.readInt();
          com.iposprinter.iposprinterservice.IPosPrinterCallback _arg5;
          _arg5 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
          this.printBarCode(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_printQRCode:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          int _arg1;
          _arg1 = data.readInt();
          int _arg2;
          _arg2 = data.readInt();
          com.iposprinter.iposprinterservice.IPosPrinterCallback _arg3;
          _arg3 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
          this.printQRCode(_arg0, _arg1, _arg2, _arg3);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_printRawData:
        {
          data.enforceInterface(descriptor);
          byte[] _arg0;
          _arg0 = data.createByteArray();
          com.iposprinter.iposprinterservice.IPosPrinterCallback _arg1;
          _arg1 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
          this.printRawData(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_sendUserCMDData:
        {
          data.enforceInterface(descriptor);
          byte[] _arg0;
          _arg0 = data.createByteArray();
          com.iposprinter.iposprinterservice.IPosPrinterCallback _arg1;
          _arg1 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
          this.sendUserCMDData(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_printerPerformPrint:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          com.iposprinter.iposprinterservice.IPosPrinterCallback _arg1;
          _arg1 = com.iposprinter.iposprinterservice.IPosPrinterCallback.Stub.asInterface(data.readStrongBinder());
          this.printerPerformPrint(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements com.iposprinter.iposprinterservice.IPosPrinterService
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
      @Override public int getPrinterStatus() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getPrinterStatus, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getPrinterStatus();
          }
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void printerInit(com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_printerInit, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().printerInit(callback);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void setPrinterPrintDepth(int depth, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(depth);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setPrinterPrintDepth, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setPrinterPrintDepth(depth, callback);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void setPrinterPrintFontType(java.lang.String typeface, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(typeface);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setPrinterPrintFontType, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setPrinterPrintFontType(typeface, callback);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void setPrinterPrintFontSize(int fontsize, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(fontsize);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setPrinterPrintFontSize, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setPrinterPrintFontSize(fontsize, callback);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void setPrinterPrintAlignment(int alignment, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(alignment);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setPrinterPrintAlignment, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setPrinterPrintAlignment(alignment, callback);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void printerFeedLines(int lines, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(lines);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_printerFeedLines, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().printerFeedLines(lines, callback);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void printBlankLines(int lines, int height, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(lines);
          _data.writeInt(height);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_printBlankLines, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().printBlankLines(lines, height, callback);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void printText(java.lang.String text, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(text);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_printText, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().printText(text, callback);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void printSpecifiedTypeText(java.lang.String text, java.lang.String typeface, int fontsize, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(text);
          _data.writeString(typeface);
          _data.writeInt(fontsize);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_printSpecifiedTypeText, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().printSpecifiedTypeText(text, typeface, fontsize, callback);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void PrintSpecFormatText(java.lang.String text, java.lang.String typeface, int fontsize, int alignment, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(text);
          _data.writeString(typeface);
          _data.writeInt(fontsize);
          _data.writeInt(alignment);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_PrintSpecFormatText, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().PrintSpecFormatText(text, typeface, fontsize, alignment, callback);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void printColumnsText(java.lang.String[] colsTextArr, int[] colsWidthArr, int[] colsAlign, int isContinuousPrint, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStringArray(colsTextArr);
          _data.writeIntArray(colsWidthArr);
          _data.writeIntArray(colsAlign);
          _data.writeInt(isContinuousPrint);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_printColumnsText, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().printColumnsText(colsTextArr, colsWidthArr, colsAlign, isContinuousPrint, callback);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void printBitmap(int alignment, int bitmapSize, android.graphics.Bitmap mBitmap, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(alignment);
          _data.writeInt(bitmapSize);
          if ((mBitmap!=null)) {
            _data.writeInt(1);
            mBitmap.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_printBitmap, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().printBitmap(alignment, bitmapSize, mBitmap, callback);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void printBarCode(java.lang.String data, int symbology, int height, int width, int textposition, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(data);
          _data.writeInt(symbology);
          _data.writeInt(height);
          _data.writeInt(width);
          _data.writeInt(textposition);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_printBarCode, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().printBarCode(data, symbology, height, width, textposition, callback);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void printQRCode(java.lang.String data, int modulesize, int mErrorCorrectionLevel, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(data);
          _data.writeInt(modulesize);
          _data.writeInt(mErrorCorrectionLevel);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_printQRCode, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().printQRCode(data, modulesize, mErrorCorrectionLevel, callback);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void printRawData(byte[] rawPrintData, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeByteArray(rawPrintData);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_printRawData, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().printRawData(rawPrintData, callback);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void sendUserCMDData(byte[] data, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeByteArray(data);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_sendUserCMDData, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().sendUserCMDData(data, callback);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void printerPerformPrint(int feedlines, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(feedlines);
          _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_printerPerformPrint, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().printerPerformPrint(feedlines, callback);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      public static com.iposprinter.iposprinterservice.IPosPrinterService sDefaultImpl;
    }
    static final int TRANSACTION_getPrinterStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_printerInit = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_setPrinterPrintDepth = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_setPrinterPrintFontType = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_setPrinterPrintFontSize = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_setPrinterPrintAlignment = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
    static final int TRANSACTION_printerFeedLines = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
    static final int TRANSACTION_printBlankLines = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
    static final int TRANSACTION_printText = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
    static final int TRANSACTION_printSpecifiedTypeText = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
    static final int TRANSACTION_PrintSpecFormatText = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
    static final int TRANSACTION_printColumnsText = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
    static final int TRANSACTION_printBitmap = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
    static final int TRANSACTION_printBarCode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
    static final int TRANSACTION_printQRCode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
    static final int TRANSACTION_printRawData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
    static final int TRANSACTION_sendUserCMDData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
    static final int TRANSACTION_printerPerformPrint = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
    public static boolean setDefaultImpl(com.iposprinter.iposprinterservice.IPosPrinterService impl) {
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
    public static com.iposprinter.iposprinterservice.IPosPrinterService getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  public int getPrinterStatus() throws android.os.RemoteException;
  public void printerInit(com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
  public void setPrinterPrintDepth(int depth, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
  public void setPrinterPrintFontType(java.lang.String typeface, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
  public void setPrinterPrintFontSize(int fontsize, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
  public void setPrinterPrintAlignment(int alignment, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
  public void printerFeedLines(int lines, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
  public void printBlankLines(int lines, int height, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
  public void printText(java.lang.String text, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
  public void printSpecifiedTypeText(java.lang.String text, java.lang.String typeface, int fontsize, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
  public void PrintSpecFormatText(java.lang.String text, java.lang.String typeface, int fontsize, int alignment, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
  public void printColumnsText(java.lang.String[] colsTextArr, int[] colsWidthArr, int[] colsAlign, int isContinuousPrint, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
  public void printBitmap(int alignment, int bitmapSize, android.graphics.Bitmap mBitmap, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
  public void printBarCode(java.lang.String data, int symbology, int height, int width, int textposition, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
  public void printQRCode(java.lang.String data, int modulesize, int mErrorCorrectionLevel, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
  public void printRawData(byte[] rawPrintData, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
  public void sendUserCMDData(byte[] data, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
  public void printerPerformPrint(int feedlines, com.iposprinter.iposprinterservice.IPosPrinterCallback callback) throws android.os.RemoteException;
}
