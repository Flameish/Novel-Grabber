# Novel-Grabber
Novel-Grabber is a GUI based web scrapper that can download chapters from various supported web/light novel sites or from any other site manually. Furthermore,  Novel-Grabber can poll a list of novels for new chapter releases and notify you with desktop notifications.

## Natively supported sites
* [Wuxiaworld](https://wuxiaworld.com/)
* [Royalroad](https://royalroad.com/)
* [Gravitytales](https://gravitytales.com/)
* [Volarenovels](https://volarenovels.com/)
* [Light Novels Translations](https://lightnovelstranslations.com/)
* [WordExcerpt](https://wordexcerpt.com/)
* [BoxNovel](https://boxnovel.com/)

## How to use
<strong>Automatic:</strong>

1. Download and execute the [NovelGrabber.jar](https://github.com/Flameish/Novel-Grabber/releases/latest) of the latest release
2. Enter the link to the novel's Table of Contents page
3. Pick the corresponding host
4. Choose your save directory
5. Select the desired chapter range
6. Pick from various options
7. Grab chapters
8. <a href="#converting">Convert Table of Contents file to desired format in Calibre</a>

* Options:

   <b>Create ToC</b> will create a Table of Contents file with links to all downloaded chapters. This file can be used in [Calibre](https://calibre-ebook.com/) to convert the chapters into a single epub file or any other supported format.

   <b>Chapter Numeration</b> will add a number in front of the file names to keep them in order if they don't come with one in their names.
   
   <b>Invert chapter order</b> will start to download the last chapter from the Table of Contents page first. Helpful if sites list the most recent chapter at the top.
   
   <b>Get images</b> downloads potential images in a chapter. They will be displayed after <a href="#converting">converting with Calibre</a>.
   
   <b>Wait time</b> is the time between each chapter call in milliseconds (1000 for 1 second) (so you don't flood the host server with requests)<br>
   ![alt text](https://i.imgur.com/OOzg8aR.png)

<strong>Manual:</strong>

<small>(if your novel is not on one of the natively supported sites)</small><br><br>
Using a Table of Content URL:</strong>
1. Enter the URL to the novel's "Table of Contents" page and retrieve all links on the site. 

2. Afterwards remove all unwanted links from the selection window. You can use shift/crtl-select like you normally would anywhere else. (Note: The chapters should be hosted on the same site or the chapter-text selectors will probably not work)
![alt text](https://i.imgur.com/bLSiaJ6.gif)

3. Input the chapter container wrapper the site uses. (Inspect a chapter page in your browser and look for something with which the container is identifiable. <br>
For example a `<div>` with a class `chapter-text` or id `chapter-content`. <br>
If you go with a class name, type a `.` in front of the name (`.chapter-text`) or a `#` for ids (`#chapter-content`). <br>
<small><a href="https://i.imgur.com/NGWjmUo.gif">(Showcase)</a></small><br>
You can find more complex selector examples and information on the official [jsoup site](https://jsoup.org/cookbook/extracting-data/selector-syntax).<br>

Using Chapter-To-Chapter navigation:
1. Input the URL of your starting point and ending point chapter. (Inclusive)
2. Input the selector for the "Next-Chapter" button. You want to select the `<a>` tag of it. Works exactly like the chapter-container selector of step 3.
3. Same as the other step 3.

## <span id="converting">Converting with Calibre</span>
<small>(click to enlarge)</small><br>
https://i.imgur.com/DBtrXPh.gif
![alt text]()<br>

## Disclaimer & Warning
Most sites prohibit the scrapping for their content. Use at your own risk. 
Please use with appropriate wait times. Downloaded chapters are for private use only.

## Requirements
* [Java](https://www.java.com/en/) (version 8+) needs to be installed.

## Credits & Libraries 
Novel Grabber was build in java with: <br>
 * [jsoup](https://www.jsoup.org/)
* [json-simple](https://code.google.com/archive/p/json-simple/)
