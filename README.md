# Index
Winter - UWT CSS 445 Database Project

## Features
* User login, register, and logout. SessionId are kept to automatically log a user in when they close the app.
* Index main view shows a list of items that you have gotten over time. There is also a "(+)" button so you can add a item.
* Clicking the "(+)" button will open up your camera application so you can scan a barcode. Scanning a barcode will automatically fill in some information about your item such as item name, barcode number, and item description. 
* Clicking on an item in the main view will show you information about your item. 
* In the item detail view you can view, edit and delete your item. To delete your item, you must first click on the edit button.
* The main view also has a sliding navigation menu where you can logout. 

## Features that did not make it
* Images for items.
* Scraping for an items MSRP/average price.
* User statistics page to show total spent, number of items, etc.
* Filter items by categories. Items do not have a category at the moment. 
* Generating specific QRCodes for items so you can print and scan to find information about it.

## Implementation
Index was made using AndroidStudio. It is executing SQL code by opening an http url connection and getting the JSONArray results from a specific link. However, the link is public and not secure. This will allow any person with the link to inject SQL code and do select, insert, update and delete on data in any available tables for the Index DB. The http connection is also not using POST to hide the SQL code being sent from my app to the web page. I do not reccomend or would advise anyone to run SQL code this way, however, do to the limited time and circumstances (such as all IPs trying to connect to a student MySQL database being blocked if your IP address is outside of the University of Washington IP range), this is my work around to connect to my MySQL DB. 
* MySQL code can be found in the following files: AddItemActivity.java, ItemDetailActivity.java, Login.java, Regsiter.java, Item.java, ItemDescription.java, UserInventory.java, UserItem.java 

## Instructions to side load the project

### Method 1
* Clone the project to your local machine.
* Using Android Studio, open an existing project.
* Connect your phone to your computer.
  * Make sure you have USB debugging turned on and have the proper drivers so your computer can detect your phone.
* Run my project by hitting SHIFT+F10 or clicking the green arrow button.
* A box will pop up asking you to choose a device and your phone should be listed. 
* Choose your phone and press "OK"

### Method 2
* Go to the following directory: Index/app/build/outputs/apk
* Inside you will see app-debug.apk and app-debug-unaligned.apk
* Copy app-debug.apk into your phone. You can do this by attaching your phone to the computer and dragging it into the root of your phone or placing it in dropbox, emailing, etc. 
* Once app-debug.apk is on your phone, use a file manager to find the apk file. 
* Once found, open the apk file.
  * Note: To install from an apk file, you must enable "Unknown sources" which allows installation of apps from unknown sources. To do this, go to the settings menu then security. If you scroll down to "Device administration" you will see "Unknown sources." Make sure that is turned on.
* Install the apk file and then you should be able to find the app inside your app drawer. 
