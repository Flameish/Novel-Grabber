# Novel-Grabber
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
* Download and execute the [NovelGrabber.jar](https://github.com/Flameish/Novel-Grabber/releases/latest) of the latest release
* Enter the link to the novel's Table of Contents page
* Pick the corresponding host
* Choose your save directory
* Select the desired chapter range 
* Pick from various options
* Grab chapters
* <a href="#converting">Convert Table of Contents file to desired format in Calibre</a>

Options:
* "Create ToC" will create a Table of Contents file with links to all downloaded chapters. This file can be used in [Calibre](https://calibre-ebook.com/) to convert the chapters into a single epub file or any other supported format.
* "Chapter Numeration" will add a number in front of the file names to keep them in order if they don't come with one in their names.
* "Invert chapter order" will start to download the last chapter from the Table of Contents page first. Helpful if sites list the most recent chapter at the top.
* Normally Novel-Grabber only saves text from within paragraph tags. This is usually sufficient to provide a good chapter content coverage.
  
  But if some chapters display content in different ways, "Ignore sentence selector" will get all text from the chapter container instead.
  <p>Examples when it is needed: <br> 
   - Sentences are not embedded in paragraph tags. <br>
   - Information is displayed in a table (spreadsheet). VRMMO novel translators like to do this.</p>
   Drawback: Imprecise content selection. If sites display a "Next Chapter" button within the same div, its display text will also be saved.
   
* To not flood the server, specify a wait time in milliseconds. (1000 for 1 second) Novel-Grabber will wait that long between each chapter call.
* Currently, chapters can only be saved as HTML or TXT files since converting can be done in Calibre.

![alt text](https://i.imgur.com/OOzg8aR.png) <br>

Alternatively use the manual tab if your novel is not on one of the supported sites:
* Enter the URL to the novel's "Table of Contents" page and retrieve all links on the site. Afterwards remove all unwanted links from the selection window. You can use shift/crtl-select like you normally would anywhere else. (Note: The chapters should be hosted on the same site or the chapter-text selectors will probably not work)

* Input the chapter wrapper the site uses. (Inspect a chapter page in your browser and look for something with which the container is identifiable. For example a \<div\> with a class "chapter-text" or id "chapter-content". If you go with a class name use a "." in front of the name (.chapter-text) or a "#" for ids (#chapter-content). You can find more complex selector examples and information on the official [jsoup site](https://jsoup.org/cookbook/extracting-data/selector-syntax).
* While you are inspecting the webpage, take a look at the wrapper around each chapter's sentences (if there are any). Most commonly used is "\<p\>". Input just "p" if that is the case.
* Select "Don't use a sentence selector" if you are unsure or the sentences are not embedded in paragraph tags.

![alt text](https://i.imgur.com/bLSiaJ6.gif)<br>

## <span id="converting">Converting with Calibre</span>
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
 *[jsoup](https://www.jsoup.org/)
* [json-simple](https://code.google.com/archive/p/json-simple/)
