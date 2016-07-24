# CleanSlate

This project is inspired by the following original work.

_Photomontage_ [http://grail.cs.washington.edu/projects/photomontage/] (http://grail.cs.washington.edu/projects/photomontage/)

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

Computing Visual Correspondence with Occlusions using Graph Cuts by Vladimir Kolmogorov and Ramin Zabih.

Link: [http://pub.ist.ac.at/~vnk/papers/KZ01.html] (http://pub.ist.ac.at/~vnk/papers/KZ01.html)

### Scope of the Project

Both projects made their implementations available. They are both written in c++. The goal of this project is to integrate their works into an android phone so that we can take pictures with the phone and get the 'CleanSlate' result in the moment. Thus, the project mainly involves extraction and translation of original works. I made the best effort to credit the original work in each classes that refer the works mentioned above.
