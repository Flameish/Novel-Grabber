# ![alt text](https://i.imgur.com/LrV2tLe.png)Novel-Grabber
Novel-Grabber is a GUI based web scrapper that can download chapters from various supported web/light novel sites or from any other site manually. Furthermore,  Novel-Grabber can poll a list of novels for new chapter releases and notify you with desktop notifications.

## Natively supported sites
* [Wuxiaworld](https://wuxiaworld.com/)
* [Royalroad](https://royalroad.com/)
* [Gravitytales](https://gravitytales.com/)
* [Volarenovels](https://volarenovels.com/)
* [BoxNovel](https://boxnovel.com/)
* [Lightnovel Translations](https://lightnovelstranslations.com/)
* [Noodletown Translated](https://noodletowntranslated.com/)
* [Exiled Rebels Scanlations](https://exiledrebelsscanlations.com/) 

## How to use
<strong>Automatic:</strong>

1. Download and execute the [NovelGrabber.jar](https://github.com/Flameish/Novel-Grabber/releases/latest) of the latest release
2. Enter the link to the novel's Table of Contents page
3. Pick the corresponding host
4. Choose your save directory
5. Select the desired chapter range
6. Pick from various options
7. Grab chapters
8. <a href="#converting-with-calibre">Convert Table of Contents file to desired format in Calibre</a>

* Options:

   <b>Create ToC</b> will create a Table of Contents file with links to all downloaded chapters. This file can be used in [Calibre](https://calibre-ebook.com/) to convert the chapters into a single epub file or any other supported format.

   <b>Chapter Numeration</b> will add a number in front of the file names to keep them in order if they don't come with one in their names.
   
   <b>Invert chapter order</b> will start to download the last chapter from the Table of Contents page first. Helpful if sites list the most recent chapter at the top.
   
   Pick one of 3 different <b>text selection</b> methods:
     
  * <b>Paragraph text</b> will only get text from within `<p>` tags.
  * <b>Pure text</b> will get all text from the chapter content `<div>` and will not be formated.
  * <b>Everything</b> will just get the whole HTML code of the chapter content `<div>`. Text will be <em>formated</em> like the webpage's. This method will also download and display potential images in the final output format <small><small>(EPUB, MOBI, PDF etc)</small></small>.<br>
   
   <b>Wait time</b> is the time between each chapter call in milliseconds (1000 for 1 second) (so you don't flood the host server with requests)<br>![alt text](https://i.imgur.com/OOzg8aR.png) <br>

<strong>Manual:</strong>

<small>(if your novel is not on one of the natively supported sites)</small>
1. Enter the URL to the novel's "Table of Contents" page and retrieve all links on the site. 

2. Afterwards remove all unwanted links from the selection window. You can use shift/crtl-select like you normally would anywhere else. (Note: The chapters should be hosted on the same site or the chapter-text selectors will probably not work)<br>![alt text](https://i.imgur.com/bLSiaJ6.gif)<br>

3. Input the chapter wrapper the site uses. (Inspect a chapter page in your browser and look for something with which the container is identifiable. <br>
For example a `<div>` with a class `chapter-text` or id `chapter-content`. <br>
If you go with a class name, type a `.` in front of the name (`.chapter-text`) or a `#` for ids (`#chapter-content`). <br>
<small><a href="https://i.imgur.com/NGWjmUo.gif">(Showcase)</a></small><br>
You can find more complex selector examples and information on the official [jsoup site](https://jsoup.org/cookbook/extracting-data/selector-syntax).<br>

## Converting with Calibre
<small>(click to enlarge)</small><br>
![alt text](https://i.imgur.com/DBtrXPh.gif)<br>

## Currently not working
* Websites which load chapter lists etc. later with javascript or in other ways.

## Disclaimer & Warning
Most sites prohibit the scrapping for their content. Use at your own risk. 
Please use with appropriate wait times. Downloaded chapters are for private use only.

## Requirements
* [Java](https://www.java.com/en/) (version 8+) needs to be installed.

## Credits & Libraries 
Novel Grabber was build in java with: <br>
 * [jsoup](https://www.jsoup.org/)
* [json-simple](https://code.google.com/archive/p/json-simple/)
