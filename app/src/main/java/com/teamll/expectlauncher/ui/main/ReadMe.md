# Nhận danh sách App từ AppLoader 
### Bước 1 : Tạo một biến ArrayList lưu mảng danh sách App 

```
 private ArrayList<App> apps = new ArrayList<>();
```
### Bước 2 : Implement AppLoaderActivity.AppDetailReceiver và nạp đè 2 hàm trong đó

##### onLoadComplete sẽ được gọi và cho ta danh sách App, thêm nó vào danh sách
``` 
@Override
    public void onLoadComplete(ArrayList<App> data) {
       apps.clear();
       apps.addAll(data);
       // TODO : Add something here 
}
```
##### onLoadReset sẽ được gọi khi danh sách App bị đổi, ta sẽ không làm gì cả 
```
@Override
    public void onLoadReset() {
    }
```
### Bước 3 :gọi hàm đăng ký lắng nghe vào cuối phương thức onViewCreated() 
```
 @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
       super.onViewCreated(view,savedInstanceState);

       // view.findViewByID ở đây, làm một vài thứ khởi tạo ở đây thay vì onCreateView 
       ...

      // đăng ký lắng nghe 
     AppLoaderActivity ac = (AppLoaderActivity)getActivity();
     ac.addAppDetailReceiver(this);
}
```
## Xong 
