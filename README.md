# Food Orders Generator Android Application
An Android application that displays a dynamically-oriented random-generated food order based on the user's input of number of items and toppings to order.

## Prerequisites
* An Android environment

## Using the application
On startup, the main window will load:

![main window](https://user-images.githubusercontent.com/45856261/64326700-1b9f8900-cfd3-11e9-83dc-960cf4855531.PNG)

Enter your desired amount of items and toppings. The number of toppings is the same for each food item:

![entering numbers](https://user-images.githubusercontent.com/45856261/64326699-1b06f280-cfd3-11e9-99db-63e53b0534a1.PNG)

Lastly, choose the type of food you would like to order from the drop down list - either pizza or sushi (future options will be added):

![drop down list](https://user-images.githubusercontent.com/45856261/64326703-1c381f80-cfd3-11e9-8d1a-c2d6bfff23ce.PNG)

Once you chooose your desired food type to order, the list of available toppings will display. You can configure each of the fields to your liking.
Notice how the button is enabled only after you have entered all the required fields.


Click the button to generate your order and scroll down to see your order! The algorithm generates different food items based on your input and displays them dynamically. It goes through all the toppings available and chooses those who haven't been selected before. Once all the toppings have been used, it will start a new "cycle" of food items and create new and different items:

![order display](https://user-images.githubusercontent.com/45856261/64326702-1b9f8900-cfd3-11e9-9874-b65ac2dd921c.PNG)

Choosing more food items than the window can handle will cause the new items to move to a new line:

![multiple lines](https://user-images.githubusercontent.com/45856261/64326701-1b9f8900-cfd3-11e9-9a45-c4c0a54d6718.PNG)

