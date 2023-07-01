package com.multiplatform.helper;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.TransferListener;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by michaeldunn on 3/13/17.
 */

public class EncryptedFileDataSourceFactory implements DataSource.Factory {

  private Cipher mCipher;
  private SecretKeySpec mSecretKeySpec;
  private IvParameterSpec mIvParameterSpec;
  private TransferListener mTransferListener;
  DataSource dataSource;

  public EncryptedFileDataSourceFactory(DataSource dataSource, Cipher cipher, SecretKeySpec secretKeySpec, IvParameterSpec ivParameterSpec, TransferListener listener) {
    mCipher = cipher;
    mSecretKeySpec = secretKeySpec;
    mIvParameterSpec = ivParameterSpec;
    mTransferListener = listener;
    this.dataSource=dataSource;
  }




  @Override
  public EncryptedFileDataSource createDataSource() {
    return new EncryptedFileDataSource(dataSource,mCipher, mSecretKeySpec, mIvParameterSpec, mTransferListener);
  }
}