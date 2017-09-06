# Java/Android中的四大引用


引用分为四个,从高到低的级别以此为**强引用**-**软引用**-**弱引用**-**虚引用**.

- 引用类型

    | 类别        | 回收机制  |  用途|生存时间|
    | :--------:  | :-----:  | :--:|:---|
    | 强引用        | 从不回收      |  对象状态| JVM停止运行时|
    | 软引用        | 内存不足时进行回收      | 缓存|  内存不足|
    | 弱引用       | 对象不被引用时回收     |  缓存|GC运行后|
    | 虚引用       | 对象被回收时      | 管理控制精确内存稳定性 |unknown|
    
### 强引用

```
Qiang qiang=new Qiang();
Niu niu=new Niu(qiang)
```
强引用例子,niu持有qiang的引用,当qiang=null的时候,并不能回收,而niu需要qiang,导致内存泄漏,典型的引用泄漏.

特点：
 - 即便OOM也不会发生回收.
 - 强引用在引用对象null时会导致内存泄漏
 - 强引用可以直接访问目标对象
 
### 软引用

```
    A a = new  A();
    SoftReference aSoftRef=new SoftReference(a);

    A a1=(A)a.get();
```

一个对象只具有软引用，则内存空间足够，垃圾回收器就不会回收它；如果内存空间不足了，就会回收这些对象的内存。
**只要垃圾回收器没有回收它，该对象就可以被程序使用。**

软引用可以和一个引用队列（ReferenceQueue）联合使用，如果软引用所引用的对象被垃圾回收，Java虚拟机就会把这个软引用加入到与之关联的引用队列中。

```
    ReferenceQueue queue = new  ReferenceQueue();
    SoftReference  softReference=new  SoftReference(软引用对象, queue);
```

#### Android中的示例

 - 案例1:
 
在平常Android开发中,有很多的图片要显示,如果是网络的则通过网络解析获取,如果
每次都从网络解析影响体验,那么我们会将其保存到本地,如果每次从本地获取相对于我们将获取后的图片缓存下来直接从内
存中获取效率更低.但是因为图片的数量多,消耗内存过大,缓存图片的过程需要大量的内存,内存不够则会OOM,这时便可以采用软引用的技术来解决问题.

```
 private Map<String,SoftReference> softReferenceMap=new HashMap<>();


    /**
     *
     * @param path
     */
    public  void addBitmap(String path){
        // 强引用的Bitmap对象
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        // 软引用的Bitmap对象
        SoftReference<Bitmap> softBitmap = new SoftReference<Bitmap>(bitmap);
        // 添加softBitmap到Map中使其缓存
        softReferenceMap.put(path, softBitmap);
    }

    /**
     *
     * @param path
     * @return
     */
    public Bitmap getBitmap(String path) {
        // 从缓存中取软引用的Bitmap对象
        SoftReference<Bitmap> softBitmap = softReferenceMap.get(path);
        // 判断是否存在软引用
        if (softBitmap == null) {
            return null;
        }
        // 取出Bitmap对象，如果由于内存不足Bitmap被回收，将取得空
        Bitmap bitmap = softBitmap.get();
        return bitmap;
    }

```

在softBitmap.get()中获取Bitmap的实例的强引用,在内存充足的情况下不会回收软引用对象,可以取出bitmap

内存不足时,softBitmap.get()不在返回bitamp直接返回null,软引用被回收了 

因此在获取Bitmap的对象之前要判断softBitmap == null是否为空,负责将会出现空指针异常.

- 案例2

设计给出了一张1080的全屏图片,这张图片假设500K,可以想象它的内存消耗,通用使用软引用来解决
```
public class SoftReferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView imageView=new ImageView(this);
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inPreferredConfig= Bitmap.Config.ARGB_4444;
        options.inPurgeable= true;
        options.inInputShareable= true;
        options.outWidth= 720;
        options.outHeight= 1280;
        Bitmap bitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher_round,options);
        Drawable drawable=new BitmapDrawable(getResources(),bitmap);
        SoftReference<Drawable> drawableSoftReference =
                new SoftReference<Drawable>(drawable);
        if(drawableSoftReference != null) {
            //内存不足将不显示
            imageView.setBackground(drawableSoftReference.get());
        }

    }
}
```

因此在特定的场景想要避免OOM的发生就尽量使用软引用吧.

但是在Android中最好选择Least Recently Used(LRU),在它内部维护一个特定大小内存,在内存不足时会根据一系列的策略算法来进行处理移除掉当前一些缓存以便获取新的内存空间用来缓存数据.

### 弱引用

```
 WeakReference<MainActivity> weakReference = new WeakReference<MainActivity>(new MainActivity()) ;
```
如果一个对象只具有弱引用，那么在垃圾回收器线程扫描的过程中，一旦发现了只具有弱引用的对象，不管当前内存空间足够与否，都会回收它的内存.

不过，由于垃圾回收器是一个优先级很低的线程，因此不一定会很快发现那些只具有弱引用的对象。

弱引用可以和一个引用队列（ReferenceQueue）联合使用，如果弱引用所引用的对象被垃圾回收，Java虚拟机就会把这个弱引用加入到与之关联的引用队列中。

#### Android中的示例

在最常见的Handler持有一个Activity的引用,Handler作为一个耗时的异步线程处理,如果在处理过程中把Activity关闭了,因为Handler还持有Activity的引用,而一个异步线程持有Handler引用,那么就将导致内存泄漏

解决方案:

- 在Activity关闭的地方将**线程停止**以及把Handler的**消息队列的所有消息对象移除**


- Handler改为静态类

这里我们使用将Handler改为静态类,但是因为静态类无法持有外部引用,就需要建立一个队Activity的弱引用了

```
public class WeakReferenceActivity extends AppCompatActivity {

    private WeakReference<WeakReferenceActivity> reference;
    private MyHandler myHandler = new MyHandler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myHandler.sendEmptyMessage(1);
    }


    public static class MyHandler extends Handler {
        private WeakReference<WeakReferenceActivity> reference;

        public MyHandler(WeakReferenceActivity activity) {
            reference = new WeakReference<WeakReferenceActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    WeakReferenceActivity weakReferenceActivity = (WeakReferenceActivity) reference.get();
                    if (weakReferenceActivity != null) {
                        System.out.print("WeakReferenceActivity");
                    }
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacksAndMessages(null);
    }
}
```

### 虚引用
在对象销毁时会被回收.

在Java中GC的运行时间是不确定的,在Java里有一个finalize方法,在垃圾回收器准备释放内存的时候，会先调用finalize().但因为内存还没有好耗尽,拟机不能保证在适当的时机调用finalize,因此垃圾回收期与finalize是不可靠的方法.

这时可以采用虚引用.比如一个固定的内存,在明确知道一个bitmap回收之后会释放一部分内存,新释放开辟的内存就可以让其他bitmap来使用,以此循环来达到内存的稳定性控制.


## 总结

在Android中比较常用便是 **弱引用**和**软引用**了。

对于一些OOM等常规处理使用软引用便可很好的解决,可以实现高速缓存.

对于偶尔使用的对象,并且随时获取到便使用弱引用来标记

软引用与弱引用的区别：只含有弱引用的对象的生命周期更短。在垃圾回收器线程扫描它所管辖的内存区域的过程中，一旦发现了只具有弱引用的对象，不管当前内存空间足够与否，都会回收它的内存。不过，由于垃圾回收器是一个优先级很低的线程，因此不一定会很快发现那些只具有弱引用的对象。

虚引用只要用于内存的精准控制,如Android中的ViewPager图片的查看等等

