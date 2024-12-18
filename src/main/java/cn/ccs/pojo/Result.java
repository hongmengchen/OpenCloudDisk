package cn.ccs.pojo;

import lombok.Data;

/**
 * 通用返回结果类，用于封装接口返回数据
 *
 * @param <T> 泛型参数，表示可以返回不同类型的数据
 */

@Data
public class Result<T> {
    // 状态码，，用于表示接口返回的状态
    private int code;

    // 是否成功，用于直观表示操作是否成功
    private boolean success;

    // 返回数据，可以是任意类型，由调用方指定
    private T data;

    // 提示信息，用于返回给客户端的提示信息
    private String msg;

    // 无参构造函数，用于创建一个空的Result对象
    public Result() {
    }

    /**
     * 有参构造函数，用于创建一个带有状态码、成功标志和提示信息的Result对象
     *
     * @param code    状态码，表示接口返回的状态
     * @param success 是否成功，表示操作是否成功
     * @param msg     提示信息，向客户端提供操作结果的描述
     */
    public Result(int code, boolean success, String msg) {
        super();
        this.code = code;
        this.success = success;
        this.msg = msg;
    }
}
