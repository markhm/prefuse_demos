# Prefuse demos and varations
Demos and variations based on Prefuse.

## FisheyeFilter bug in TreeView
### Introduction
This example shows a bug in/related to Prefuse's FisheyeFilter algorithm, which causes some edges at fisheye depth level 0 not to be drawn.
Note that this is a quick and dirty rework of the standard TreeView demo to easily show the problem.

    'prefuse.demos.bugs.fisheye.TreeViewFisheyeBug'

### Steps to reproduce
1. Start the 'TreeViewFisheyeBug' application, which contains two extensions, a checkbox and a button, to help show the problem.
2. Search for 'Lion' and observe there are five results.
3. Click the checkbox to expand the tree that will show the search results in the tree.
4. If necessary, pan to show the now visual part of the tree in full screen.
5. Click the button, which sets the Fisheye filter depth from 1 to 0, omitting any nodes that are not directly between one of the search results and the root.
6. Observe that some of the edges that were visible earlier are missing now (!).

Children back to their parents:
    Things -> Categories
    Organisations -> Specific People
    Marine -> Placental
    Generic -> People

Comments are appreciated.