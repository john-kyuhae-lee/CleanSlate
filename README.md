# CleanSlate

### Introduction

This project is inspired by the following original work.

_Interactive Digital Photomontage_ by Aseem Agarwala, Mira Dontcheva, Maneesh Agrawala, Steven Drucker, Alex Colburn, Brian Curless, David Salesin, and Michael Cohen

Link: [http://grail.cs.washington.edu/projects/photomontage/] (http://grail.cs.washington.edu/projects/photomontage/)

One of features is called 'clean slate'. The objective of this program is to remove moving objects from a set of pictures of the same scene. A good usecase is remove all people from a scene of popular travel places.

See narrative here:
https://docs.google.com/presentation/d/1tS6luh4BLKdJt1vY4zuIWG586or6I1nyF9ml1gn2GX0/pub?start=false&loop=false&delayms=10000

### Examples

_Input Images_ - These are images used from the original work.

<img src="https://github.com/john-kyuhae-lee/CleanSlate/blob/master/app/src/debug/res/raw/cathedral_001.jpg" width="301" height="200">
<img src="https://github.com/john-kyuhae-lee/CleanSlate/blob/master/app/src/debug/res/raw/cathedral_002.jpg" width="301" height="200">
<img src="https://github.com/john-kyuhae-lee/CleanSlate/blob/master/app/src/debug/res/raw/cathedral_003.jpg" width="301" height="200">
<img src="https://github.com/john-kyuhae-lee/CleanSlate/blob/master/app/src/debug/res/raw/cathedral_004.jpg" width="301" height="200">
<img src="https://github.com/john-kyuhae-lee/CleanSlate/blob/master/app/src/debug/res/raw/cathedral_005.jpg" width="301" height="200">

_Output Images_ - The output image from the original work.

<img src="https://github.com/john-kyuhae-lee/CleanSlate/blob/master/app/src/debug/res/raw/result.png" width="301" height="200">

### Acknowledgement

This project also utilizes the algorithm described in the following paper.

_Computing Visual Correspondence with Occlusions using Graph Cuts_ by Vladimir Kolmogorov and Ramin Zabih.

Link: [http://pub.ist.ac.at/~vnk/papers/KZ01.html] (http://pub.ist.ac.at/~vnk/papers/KZ01.html)

_Fast Approximate Energy Minimization via Graph Cuts_ by Yuri Boykov, Olga Veksler, and Ramin Zabih

Link: [http://www.cs.cornell.edu/rdz/Papers/BVZ-pami01-final.pdf] (http://www.cs.cornell.edu/rdz/Papers/BVZ-pami01-final.pdf)

_Camera2Basic_ Google Android Tutotial.

Link: [https://github.com/googlesamples/android-Camera2Basic] (https://github.com/googlesamples/android-Camera2Basic)

### Project Scope

Both projects made their implementations available. They are both written in c++. The goal of this project is to integrate their works into an android phone so that we can take pictures with the phone and get the 'CleanSlate' result in the moment. Users may use tripod to mount phones with this app to take a picture of a scene. The app automatically takes 5-10 pictures and produces outcome that has no people present to the best effort. The project mainly involves extraction and translation of original works and integration into the android camera functionality. I made the best effort to credit the original work in each file that refers to the works cited above.

### Current Progress

Initial implementation and translations of the original works are completed. Testing device is HTC One M7. Unfortunately, it runs out of memory for creating the composite for the above example images. In order to test the overall flow before proceeding to optimization, I have created a desktop version of this work -- without an ability to use a camera. The desktop version needs to be provided with inputs programatically, meaning you need to change the path that is hardcoded in test runner.

Link: [https://github.com/john-kyuhae-lee/CleanSlateDesktop] (https://github.com/john-kyuhae-lee/CleanSlateDesktop)

Optimization is not done, yet. So it doesn't work in a phone device.

### Results

These are the result of my input pictures when I ran the code on my laptop.
So I've used the desktop version to produce following results. Find the link above. A scene is of the main entrance of the ATT park. Taken (Jul 24, 2016 at around noon).

_Input Images_ 

<img src="https://github.com/john-kyuhae-lee/CleanSlateDesktop/blob/master/src/main/resources/attpark/1.jpg" width="256" height="192">
<img src="https://github.com/john-kyuhae-lee/CleanSlateDesktop/blob/master/src/main/resources/attpark/2.jpg" width="256" height="192">
<img src="https://github.com/john-kyuhae-lee/CleanSlateDesktop/blob/master/src/main/resources/attpark/3.jpg" width="256" height="192">
<img src="https://github.com/john-kyuhae-lee/CleanSlateDesktop/blob/master/src/main/resources/attpark/4.jpg" width="256" height="192">
<img src="https://github.com/john-kyuhae-lee/CleanSlateDesktop/blob/master/src/main/resources/attpark/5.jpg" width="256" height="192">
<img src="https://github.com/john-kyuhae-lee/CleanSlateDesktop/blob/master/src/main/resources/attpark/6.jpg" width="256" height="192">
<img src="https://github.com/john-kyuhae-lee/CleanSlateDesktop/blob/master/src/main/resources/attpark/7.jpg" width="256" height="192">
<img src="https://github.com/john-kyuhae-lee/CleanSlateDesktop/blob/master/src/main/resources/attpark/8.jpg" width="256" height="192">
<img src="https://github.com/john-kyuhae-lee/CleanSlateDesktop/blob/master/src/main/resources/attpark/9.jpg" width="256" height="192">
<img src="https://github.com/john-kyuhae-lee/CleanSlateDesktop/blob/master/src/main/resources/attpark/10.jpg" width="256" height="192">
<img src="https://github.com/john-kyuhae-lee/CleanSlateDesktop/blob/master/src/main/resources/attpark/11.jpg" width="256" height="192">
<img src="https://github.com/john-kyuhae-lee/CleanSlateDesktop/blob/master/src/main/resources/attpark/12.jpg" width="256" height="192">
<img src="https://github.com/john-kyuhae-lee/CleanSlateDesktop/blob/master/src/main/resources/attpark/13.jpg" width="256" height="192">

_Output Images_

<img src="https://github.com/john-kyuhae-lee/CleanSlateDesktop/blob/master/src/main/resources/attpark_composite.jpg" width="256" height="192">

