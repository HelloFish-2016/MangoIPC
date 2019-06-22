# MangoIPC

基于Android应用的跨进程通信框架

<font size=5 color=Crimson>Step 1</font>

* 在项目根目录build.gradle中添加
    ```xml
    allprojects {
        repositories {
            ......
            maven { url 'https://jitpack.io' }
        }
    }
    ```
    
* 如果服务端和客户端在一个模块，在模块下的build.gradle中添加
    ```xml
    dependencies {
        implementation 'com.github.Mangosir:MangoIPC:1.0.1'
    }
    ```
    
  如果服务端和客户端不在同一个模块，那在各自的build.gradle中都得加上

<font size=5 color=Crimson>Step 2</font>

服务端：
* 定义一个业务接口，规定与客户端通信规则，比如
    ```java
    public interface IData {

        List sendData(String params);

        Object getStudent(String name);

    }
    ```
* 实现该接口，处理客户端请求；添加运行时注解，注解值需要与后面客户端中的保持一致；获取实例的方法必须是public static且方法名为getDefault
    ```java
    @RequestHandler("IData")
    public class DataManager implements IData {

        private static final DataManager ourInstance = new DataManager();

        public static DataManager getDefault(String params) {
            return ourInstance;
        }

        private DataManager() {}
        
        @Override
        public List sendData(String params) {
            List<String> list = new ArrayList<>();
            list.add("1");
            list.add("2");
            list.add("3");
            return list;
        }
    
        @Override
        public Student getStudent(String name) {
            return new Student(name);
        }
    }
    ```

* 在某个时刻将上面的处理客户端请求的Class进行注册
    ```java
    IPCMango.getDefault().register(DataManager.class);
    ```
    记得不需要的时候解除注册
    ```java
    IPCMango.getDefault().unRegister(DataManager.class);
    ```
* 在AndroidManifest中注册RemoteService
```xml
<service android:name="com.mango.ipcore.handler.RemoteService"
            android:exported="true"></service>
```

<font size=5 color=Crimson>Step 3</font>

客户端：

* 在Activity的onCreate中绑定远程Service
    ```java
    //服务端应用的包名
    private String mRemotePackageName = "com.mango.ipc";
    IPCMango.getDefault().bind(this,mRemotePackageName);
    ```
   在Activity销毁时解绑Service
    ```java
    IPCMango.getDefault().unBind(this);
    ```
* 新建一个接口，名字不需要与服务端接口一样，要求接口中的方法必须与其一致，包括注解值
    ```
    @RequestHandler("IData")
    public interface IData {

        List sendData(String params);

        Object getStudent(String name);

    }
    ```
* 到这里就是发起请求给远程Service
    ```java
        public void sendData(View v){
            //如果服务端会执行耗时操作，这里需要放在子线程
            //发送请求给框架提供的RemoteService
            if (iData == null) {
                iData = IPCMango.getDefault()
                            .loadRequestHandler(IData.class,"伙计");
            }
            if (iData != null) {
                List list = iData.sendData("小米");
                Object student = iData.getStudent("阿菜");
            } 
        }
    ```
    

<font size=5 color=Crimson>Step 4</font>

如果不使用RemoteService，可以在服务端自定义Service，那就先绑定服务，再发送请求

服务端：java
```
public class MyRemoteService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new IRemoteService.Stub(){

            @Override
            public IPCResponse sendRequest(IPCRequest request) throws RemoteException {

                return new IPCResponse("hello","执行方法成功",true);
            }
        };
    }
}
```

客户端：

```java
        private String mRemotePackageName = "com.mango.ipc";
        private String mRemoteServiceName = "com.mango.ipc.MyRemoteService";
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            IPCMango.getDefault().bind(this,mRemotePackageName,mRemoteServiceName);

        }
    
        public void sendMyData(View view){
            //发送请求给服务端自定义的远程Service
            IPCResponse ipcResponse = IPCMango.getDefault().sendRequest(
                IPCMango.getDefault().buildRequest(0,null,null), 
                mRemoteServiceName);
        }

```
