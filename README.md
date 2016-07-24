# CleanSlate

### Introduction

This project is inspired by the following original work.

_Interactive Digital Photomontage_ by Aseem Agarwala, Mira Dontcheva, Maneesh Agrawala, Steven Drucker, Alex Colburn, Brian Curless, David Salesin, and Michael Cohen

Link: [http://grail.cs.washington.edu/projects/photomontage/] (http://grail.cs.washington.edu/projects/photomontage/)

One of features is called 'clean slate'. The objective of this program is to remove moving objects from a set of pictures of the same scene. A good usecase is remove all people from a scene of popular travel places.

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
