# Overview
Repo: https://github.com/yun19830206/CloudShop-MicroService-Architecture
Commit: 1f9d7289bcd73f0af1a05b592b5121e9eeb3f1de

This project is **incomplete**, there is no real business logic related code most of them are just dummy code, the author only implement the architecture of different microservices. Based on the documentation and the source code, the goal for this project is used to test `Hystrix` implemented `Circuit Breaker` pattern.

The example of their business logic:
Controller
``` java
@MicroServiceDesc(serviceName="order.get")
@RequestMapping(value = "/get",method = {RequestMethod.GET})
@ResponseBody
public OrderSimulationResponse getOrderInfo(String orderId, HttpServletRequest request) {
    OrderSimulationResponse orderSimulationResponse;
    if (null == orderId) {
        orderId = request.getParameter("orderId");
    }
    if (null == orderId) {
        orderSimulationResponse = new OrderSimulationResponse(ApiResponse.DEFAULT_CODE+"","缺少必填参数，请重新输入");
    } else {
        orderSimulationResponse = new OrderSimulationResponse(ApiResponse.DEFAULT_CODE+"","获得OrderId="+orderId+"的数据成功，现在返回结果");
    }
    return orderSimulationResponse;
}
```
Utility
``` java
public class OrderSimulationResponse implements Serializable {
    private static final long serialVersionUID = 4484496266996135071L;
    private String resultMessage ;
    private Object resultObject ;

    public OrderSimulationResponse(String resultMessage, Object resultObject){
        this.resultMessage = resultMessage ;
        this.resultObject = resultObject ;
    }

    public OrderSimulationResponse(){
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public Object getResultObject() {
        return resultObject;
    }

    public void setResultObject(Object resultObject) {
        this.resultObject = resultObject;
    }

    @Override
    public String toString() {
        return "OrderSimulationResponse{" +
                "resultMessage='" + resultMessage + '\'' +
                ", resultObject=" + resultObject +
                '}';
    }
}
```

# How to Deploy
There is no document on how to run the project, the only information is that we should use `zookeeper` for service management and set up the `zookeeper` properly.

# How to Run Test
There are only a few tests located in [here](https://github.com/yun19830206/CloudShop-MicroService-Architecture/blob/master/cs_core/src/test/java/CloudShopRequestConcurrentTest.java), they are mainly used to test if `Hystrix` can be triggered in high load scenario.