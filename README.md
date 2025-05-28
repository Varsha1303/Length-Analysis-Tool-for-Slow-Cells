Length Analysis Plugin
The Length Analysis plugin is a tool designed for the analysis of the paths traversed by cells in cellular suspension. It provides information such as the distance between end points (displacement), length, and curvature of the path in pixels. This information can be used to determine the motility of cells.

How it Works
The plugin works by taking an image of cells in suspension and analyzing the paths that cells traverse over a period of time. It uses image processing techniques to track cells in the image and calculate the distance, length, and curvature of the paths they traverse.

Installation
To use the Length Analysis plugin, follow these steps:

Install Fiji (ImageJ) from the Fiji website
Download the Length Analysis plugin here
Download the dependencies (Autothreshold, MorpholibJ and Skeletonize3D Plugins)
Copy the .jar files to the plugins folder in your Fiji installation directory.
Restart Fiji
The plugin can now be accessed from the Plugins menu.
Usage
To use the Length Analysis plugin, follow these steps:

Open an image of cells in suspension in Fiji.
Click on the Length Analysis plugin in the Plugins menu
The plugin will calculate the distance, length, and curvature of the paths traversed by the selected cells.
The results can be exported as a CSV file for further analysis.
Contributing
If you'd like to contribute to the Length Analysis plugin, follow these steps:

Fork this repository.
Create a branch: git checkout -b your-feature
Make your changes and commit them: git commit -m 'your-message'
Push to the original branch: git push origin my-changes
Create a pull request.
