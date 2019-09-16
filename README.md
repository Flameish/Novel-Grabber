# ![alt text](https://i.imgur.com/LrV2tLe.png)Novel-Grabber
Novel-Grabber is a gui based web scrapper that can download and convert chapters into EPUB from various supported web/light novel sites or from any other site manually. Furthermore,  Novel-Grabber can poll a list of novels for new chapter releases and notify you with desktop notifications.


##Natively supported sites
| ​ | ​ | ​|
| --- | --- | --- | 
| [Wuxiaworld](https://wuxiaworld.com/) | [Royalroad](https://royalroad.com/) | [Gravitytales](https://gravitytales.com/)|
| [Volarenovels](https://volarenovels.com/)| [Creative Novels](https://https://creativenovels.com/.com/) | [WordExcerpt](https://wordexcerpt.com/)|
| [Wattpad](https://wattpad.com/) | [FanFiction](https://fanfiction.net/) | [BoxNovel](https://boxnovel.com/)|
| [LiberSpark](https://liberspark.com/) |[Wordrain](https://wordrain69.com/) | [Comrademao](https://comrademao.com/)|
| [Light Novels Translations](https://lightnovelstranslations.com/) | [Chrysanthemum Garden](https://chrysanthemumgarden.com/)|  |
| ​ | ​|  
## How to use
<strong>Automatic:</strong>

1. Download and execute the [NovelGrabber.jar](https://github.com/Flameish/Novel-Grabber/releases/latest) of the latest release
(If you can't execute the jar, try navigating to the jar location with cmd/terminal and use: `java -jar Novel-Grabber.jar`)
2. Enter the link to the novel's Table of Contents page
3. Pick the corresponding host
4. Click on 'Check' to see if everything is alright with your input
5. Select the desired chapter range
6. Pick from various options
7. Grab chapters

* Options:

   <b>Invert chapter order</b> will start to download the last chapter of the Table of Contents page first. Helpful if sites list the most recent chapter at the top.
   
   <b>Get images</b> downloads potential images in a chapter.
   
   <b>Export:</b> Currently you can export to EPUB or a Calibre <a href="#converting">compatible table of contents file</a>.
   
   <b>Wait time</b> is the time between each chapter call in milliseconds (1000 for 1 second) (so you don't flood the host server with requests)<br>
   ![alt text](https://i.imgur.com/CEXUTZk.png)

<strong>Manual:</strong>

<small>(if the novel is not on one of the supported sites)</small><br><br>
Using a Table of Content URL:</strong>
1. Enter the URL to the novel's "Table of Contents" page and retrieve all links on the site. 

2. Afterwards remove all unwanted links from the selection window. You can use shift/crtl-select like you normally would anywhere else. (Note: The chapters should be hosted on the same site or the chapter-text selectors will probably not work)<br>
![alt text](https://i.imgur.com/j9TbP0l.gif)

3. Input the chapter container wrapper the site uses. (Inspect a chapter page in your browser and look for something with which the container is identifiable. <br>
For example a `<div>` with a class `chapter-text` or id `chapter-content`. <br>
If you go with a class name, type a `.` in front of the name (`.chapter-text`) or a `#` for ids (`#chapter-content`). <br>
<small><a href="https://i.imgur.com/NGWjmUo.gif">(Showcase)</a></small><br>
You can find more complex selector examples and information on the official [jsoup site](https://jsoup.org/cookbook/extracting-data/selector-syntax).<br>

4. Specify potential blacklisted tags which you want removed and set metadata for the epub.

Using Chapter-To-Chapter navigation:
1. Input the URL of your starting point and ending point chapter. (Inclusive)
2. Input the selector for the "Next-Chapter" button. You want to select the `<a>` tag of it. Works exactly like the chapter-container selector of step 3.
3. Same as the other step 3.

## <span id="converting">Converting with Calibre</span>
<small>(click to enlarge)</small><br>
![alt text](https://i.imgur.com/DBtrXPh.gif)<br>

## Disclaimer & Warning
Most sites prohibit the scrapping for their content. Use at your own risk. 
Please use with appropriate wait times. Downloaded chapters are for private use only.

## Requirements
* [Java](https://www.java.com/en/) (version 8+) needs to be installed.

## Credits & Libraries 
Novel Grabber was build in java with: <br>
 * [jsoup](https://www.jsoup.org/)
 * [json-simple](https://code.google.com/archive/p/json-simple/)
 * [epublib](https://github.com/psiegman/epublib)
