# RapidX
 A powerful android library which can be make an app development easy.
 
## What's New [![](https://jitpack.io/v/fiftyonemoon/RapidX.svg)](https://jitpack.io/#fiftyonemoon/RapidX)
- Introducing Auto Scale Views.
   - Now XML view can scale automatically without providing width and height.
   - Refer [`AutoRapidConstraintLayout`](app/src/main/res/layout/rapid_constraint_layout_auto.xml).

## Contains
<details>
<summary>
<a href="https://github.com/fiftyonemoon/RapidX/tree/main/provider/src/main/java/com/fom/rapidx/provider">
Provider
</summary>
<ul>
 	<li>
	<summary>
<a href="https://github.com/fiftyonemoon/RapidX/tree/main/provider/src/main/java/com/fom/rapidx/provider">
Dialogs
</summary>
	</li>
	
</ul>
</details>
- [`Provider`](provider/src/main/java/com/fom/rapidx/provider) - Media, Files, Directory, Dialogs.
- [`Views`](views/src/main/java/com/fom/rapidx/views) - Hybrid RapidViews based on Originals.
- [`UI`](ui/src/main/java/com/fom/rapidx/ui) - Global Methods.

## Implementation

```groovy
allprojects {
	repositories {
		maven { url 'https://jitpack.io' }
	}
}
```

```groovy
//Reduce an app size by excluding unused modules from library.

implementation('com.github.fiftyonemoon:RapidX:1.0.0') {
    //exclude module: 'provider' (for extarnal use)
    //exclude module: 'views' (for scaling)
    //exclude module: 'ui' (for common use)
}
```
### Scaling Note
- Create your design with default 1080x1920 screen size.
- For better experience hide navigation and status/notification bar.

### Future
- This library is under development. More things will be added in future.
