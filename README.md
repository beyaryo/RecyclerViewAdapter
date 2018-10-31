# RecyclerViewAdapter

Simplify your work with recyclerview adapter.
Now you don't need to create recyclerview adapter for every list of data you have. With this library you just have to create viewholder and thats all.

## Download 
In your project-level `build.gradle`:
```
allprojects {
    repositories {
	...
	maven { url 'https://jitpack.io' }
    }
}
```

Add dependecies to your module-level `build.gradle`:
```
dependencies {
   implementation 'com.github.beyaryo:RecyclerViewAdapter:1.0.1'
}
```
Or if you want to use your own `com.android.support` version
```
dependencies {
   implementation ('com.github.beyaryo:RecyclerViewAdapter:1.0.1'){
      exclude group: 'com.android.support', module: 'recyclerview-v7'
      exclude group: 'com.android.support', module: 'appcompat-v7'
   }
}
```

There are 3 kinds of adapter in this library
## 1. RecyclerAdapter
It's just a common adapter. To use this you need to create your own data class and `ViewHolder`.
For example your data class is :
```java
public class User {
    private int id;
    private String name;
}
```
So your `ViewHoldler` can be like this :
```java
public class SimpleHolder extends RecyclerView.ViewHolder {
    public SimpleHolder(View itemView) {
        super(itemView);
    }
}
```
Don't forget to create a layout for your item.
Finally you can use the adapter to your `RecyclerView` :
```java
RecyclerView recyclerView = findViewById(your_recyclerview_id);
recyclerView.setAdapter(new RecyclerAdapter<SimpleHolder, User>(SimpleHolder.class, your_list, your_item_layout_id) {
    @Override
    public void onBind(SimpleHolder holder, User data, int Index) {
	// Do everything you want when the holder binded
    }
});
```
If you want to add header on your `RecyclerView`, just add your header layout in the parameter
```java
RecyclerView recyclerView = findViewById(your_recyclerview_id);
recyclerView.setAdapter(new RecyclerAdapter<SimpleHolder, User>(SimpleHolder.class, your_list, your_item_layout_id, your_header_layout_id) {
    @Override
    public void onBind(SimpleHolder holder, User data, int Index) {
	// Do everything you want when the holder binded
    }
    
    @Override
    public void onHeaderBind(View itemView) {
        // Do something here when header binded
    }
});
```
All adapter support using header.
## 2. FilterAdapter
It's like adapter #1, but you can filter the data.
```java
RecyclerView recyclerView = findViewById(your_recyclerview_id);
recyclerView.setAdapter(new FilterAdapter<SimpleHolder, User>(SimpleHolder.class, your_list, your_item_layout_id) {
    @Override
    public void onBind(SimpleHolder holder, User data, int Index) {
	// Do everything you want when the holder binded
    }

    @Override
    public boolean onFiltering(String comparator, User comparedBy) {
    	// For example you want to filter by name
	return comparedBy.getName().toLowerCase().contains(comparator);
    }

    @Override
    public void onFilterResult(int count) {
	Toast.makeText(FilterActivity.this, "Found " +count+ " user's", Toast.LENGTH_SHORT).show();
    }
});
```
To filter the data you can use `EditText` `addTextChangedListener` or whatever you want
```java
EditText editText = findViewById(your_edittext_id);
editText.addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
	((FilterAdapter)recyclerView.getAdapter())
		.getFilter()
		.filter(s.toString());
    }
});
```
## 3. PaginationAdapter
With this adapter you can handle *load more-thing* list with less effort.
First create the adapter
```java
private PaginationAdapter<SimpleHolder, User> adapter = new PaginationAdapter<SimpleHolder, User>(
        SimpleHolder.class, new ArrayList<User>(), your_item_layout_id, your_loading_layout_id) {
    @Override
    public void onBind(SimpleHolder holder, User data, int Index) {
        // Do everything you want when the holder binded
    }

    @Override
    public void onLoadingBind(View itemView, LoadState state) {
        // Do everything you want when loading is triggered
	// Or when load data process fail, show the message here
    }

    @Override
    public void loadMore(int offset) {
       	// Load your next data
    }
};
```
Then inject your `RecyclerView` to the adapter
```java
RecyclerView recyclerView = findViewById(your_recyclerview_id);
adapter.setRecyclerView(this, recyclerView, 10);
```
There are 3 state you should aware.
When you gain the data, you should call 
```java
adapter.refresh(newData);
```
**IMPORTANT!!** ``newData`` is a new list contain your new data. Use this method instead of `notifyDataSetChanged()`.

When you got error when load data, call 
```java
adapter.loadError();
```
When you want to load data again after error occured, do
```java
adapter.load();
```
When you can't get data anymore
```java
adapter.loadEnd();
```
That's all, easy right. For more info, try the sample project.
## Inspired By
- [NFramework](https://github.com/noizar/Nframework)
## License
```
MIT License

Copyright (c) 2018 Bey Aryo

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
