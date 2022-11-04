# RapidX
 A powerful android library which can be make an app development easy.
 
## What's New [![](https://jitpack.io/v/fiftyonemoon/RapidX.svg)](https://jitpack.io/#fiftyonemoon/RapidX)
- Introducing Auto Scale Views.
   - Now XML view can scale automatically without providing width and height.
   - Refer [`AutoRapidConstraintLayout`](app/src/main/res/layout/rapid_constraint_layout_auto.xml).

## Contains
<details>
<summary>
<a href="https://github.com/fiftyonemoon/RapidX/tree/main/provider/src/main/java/com/fom/rapidx/provider">Provider</a>
</summary>
	<ul>
	<li>
		<a href="https://github.com/fiftyonemoon/RapidX/blob/1968d6a1083a11baa9253c21da2f56f13af58816/provider/src/main/java/com/fom/rapidx/provider/Directory.java">Directory
		</a>: Create folder and files with contentResolver.
	</li>
	<li>
		<details>
			<summary>
			<a href="https://github.com/fiftyonemoon/RapidX/blob/main/provider/src/main/java/com/fom/rapidx/provider/Dialogs.java">Dialogs</a>
			</summary>
			<ul>
			<li>
			<a href="https://github.com/fiftyonemoon/RapidX/blob/1968d6a1083a11baa9253c21da2f56f13af58816/provider/src/main/java/com/fom/rapidx/provider/Dialogs.java#L32">Alert
			</a>: Show runtime alert dialog. Available default dialogs are Save, Exit and Delete.	
			</li>
			<li>
			<a href="https://github.com/fiftyonemoon/RapidX/blob/1968d6a1083a11baa9253c21da2f56f13af58816/provider/src/main/java/com/fom/rapidx/provider/Dialogs.java#L128">
			Progress
			</a>: Show runtime progress dialog. This dialog will created on global level so can access it from anywhere.
			</li>
			</ul>
		</details>
	</li>
	<li>
		<details>
			<summary>
			<a href="https://github.com/fiftyonemoon/RapidX/blob/main/provider/src/main/java/com/fom/rapidx/provider/Media.java">Media</a>
			</summary>
			<ul>
			<li>
			<a href="https://github.com/fiftyonemoon/RapidX/blob/1968d6a1083a11baa9253c21da2f56f13af58816/provider/src/main/java/com/fom/rapidx/provider/Media.java#L342">Images
			</a>: To fetch device photos.	
			</li>
			<li>
			<a href="https://github.com/fiftyonemoon/RapidX/blob/1968d6a1083a11baa9253c21da2f56f13af58816/provider/src/main/java/com/fom/rapidx/provider/Media.java#L341">
			Videos
			</a>: To fetch device videos.
			</li>
			<li>
			<a href="https://github.com/fiftyonemoon/RapidX/blob/1968d6a1083a11baa9253c21da2f56f13af58816/provider/src/main/java/com/fom/rapidx/provider/Media.java#L340">
			Audios
			</a>: To fetch device audios.
			</li>
			</ul>
		</details>
	</li>
	<li>
		<details>
			<summary>
			<a href="https://github.com/fiftyonemoon/RapidX/blob/main/provider/src/main/java/com/fom/rapidx/provider/Files.java">Files</a>
			</summary>
			<ul>
			<li>
			<a href="https://github.com/fiftyonemoon/RapidX/blob/1968d6a1083a11baa9253c21da2f56f13af58816/provider/src/main/java/com/fom/rapidx/provider/Files.java#L53">Editor
			</a>: Copy, Move, Delete, Rename and More.	
			</li>
			<li>
			<a href="https://github.com/fiftyonemoon/RapidX/blob/1968d6a1083a11baa9253c21da2f56f13af58816/provider/src/main/java/com/fom/rapidx/provider/Files.java#L228">
			Utils
			</a>: Share/Open File, Mimetype, Unique filename generator and More.
			</li>
			</ul>
		</details>
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
