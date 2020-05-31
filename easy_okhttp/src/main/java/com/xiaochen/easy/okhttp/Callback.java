
package com.xiaochen.easy.okhttp;

import java.io.IOException;

public interface Callback {
  /**
   * 请求失败
   * @param call
   * @param e
   */
  void onFailure(RealCall call, IOException e);

  /**
   * 请求成功
   * @param call
   * @param response
   * @throws IOException
   */
  void onResponse(RealCall call, Response response) throws IOException;
}
