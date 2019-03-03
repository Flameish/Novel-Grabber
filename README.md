# Novel-Grabber
With Novel Grabber you can download single chapters or complete novels from various web/lightnovel sites.

## Natively supported sites
* [Wuxiaworld](https://wuxiaworld.com/)
* [Royalroad](https://royalroad.com/)
* [Gravitytales](https://gravitytales.com/)
* [Volarenovels](https://volarenovels.com/)

## How to use
* Download and execute [NovelGrabber.jar](https://github.com/Flameish/Novel-Grabber/raw/master/NovelGrabber.jar)
* Enter the link to the novel's Table of Contents page
* Pick the corresponding host
* Choose your save directory
* Select the desired chapter range 

Optional:
* "Create ToC" will create a Table of Contents file with links to all downloaded chapters. This file can be used in [Calibre](https://calibre-ebook.com/) to convert the chapters into a single epub file.
* Choose between multiple file types for the chapters to be saved in. (Note: ToC can only be created from HTML files)
* Chapter Numeration will add a number in front of the file names to keep them in order if they don't come with one in their names.
* You can also enter a link to a specific chapter at the bottom. <br>

![alt text](https://i.imgur.com/zCgugtX.jpg) <br>

Alternatively use the manual tab if the novel is not on one of the supported sites(requires a little bit of HTML knowledge):
* Enter the URL to the novel's Table of Contents page and retrieve all links on the site. Afterwards remove all unwanted links from the selection window. (Note: The chapters should be hosted on the same site or the chapter-text selectors will probably not work)
* Input the chapter wrapper the site uses. (Inspect a chapter page in your browser and look for something with which the container is identifiable. For example a \<div\> with a class "chapter-text" or id "chapter-content". If you go with a class name use a "." in front of the name (eg: .chapter-text) or a "#" for ids (eg: #chapter-content).
* While you are inspecting the webpage, take a look at the wrapper around each chapter's sentences (if there are any). Most commonly used is "\<p\>". Input "p" if that is the case. <br>

![alt text](https://i.imgur.com/mIrefvb.jpg)<br>

## Currently not working
* [Coiling Dragon](https://www.wuxiaworld.com/novel/coiling-dragon-preview) - Official epub available

## Disclaimer
Downloaded chapters/files are for private use only.

## Requirements
* [Java](https://www.java.com/en/) needs to be installed.

## Credits
Novel Grabber was build in java with [jsoup](https://www.jsoup.org/) :heart:
