# ![alt text](https://i.imgur.com/LrV2tLe.png)Novel-Grabber
Novel-Grabber is a gui based web scrapper that can download and convert chapters into EPUB from various supported web/light novel sites or from any other site manually.


## Natively supported sites

| [Wuxiaworld](https://wuxiaworld.com/) | [Royalroad](https://royalroad.com/) | [FoxTeller](https://foxteller.com/)|
| :--- | :--- | :--- |
| <b>[NovelUpdates](https://novelupdates.com/)</b>| <b>[Creative Novels](https://https://creativenovels.com/.com/)</b> | <b>[WordExcerpt](https://wordexcerpt.com/)</b>|
| <b>[Dreame](https://dreame.com/)</b> | <b>[TapRead](https://www.tapread.com/)</b> | <b>[Zenith Novels](https://zenithnovels.com/)</b>|
| <b>[Kuhaku Light Novel Translations](https://kuhakulightnoveltranslations.com/)</b> | <b>[Translation Otaku](https://translatinotaku.net/)</b> | <b>[ISO Translations](https://isotls.com/)</b>|
| <b>[Chrysanthemum Garden](https://chrysanthemumgarden.com/)</b> | <b>[FanFiction](https://fanfiction.net/)</b> | <b>[BoxNovel](https://boxnovel.com/)</b>|
| <b>[LiberSpark](https://liberspark.com/)</b> |<b>[Wordrain](https://wordrain69.com/)</b> | <b>[Comrade Mao](https://comrademao.com/)</b>|
| <b>[Light Novels Translations](https://lightnovelstranslations.com/)</b> | <b>[Ebisu Translations](https://ebisutranslations.com/)</b> | <b>[Webnovel](https://webnovel.com/)</b>
| <b>[Wattpad](https://wattpad.com/)</b> | <b>[WuxiaWorld.online](https://wuxiaworld.online/)</b> | <b>[Booklat](https://booklat.com.ph/)</b>
| <b>[WuxiaWorld.site](https://wuxiaworld.site/)</b> | <b>[FicFun](https://ficfun.com/)</b> | <b>[Novel Full](http://novelfull.com/)</b> 
| <b>[Foxaholic](https://foxaholic.com/)</b> | <b>[FanFiktion](https://fanfiktion.de/)</b> | <b>[Scribble Hub](https://scribblehub.com/)</b>
| <b>[Volarenovels](https://volarenovels.com/)</b> | <b>[MTL Novel](https://www.mtlnovel.com/)</b> | 

## Features
- Extensive and easy to use manual grabbing
- Image support
- Style removal
- Unwanted content removal via blacklisted HTML tags
- EPUB metadata
- Desktop and email notifications for new chapter releases of followed novels
- Automatic chapter detection (Able to download via NovelUpdates)
- Headless browser grabbing
- Login support for certain host sites
- CLI 

## Screenshots

![Automatic Tab](https://i.imgur.com/dcEC1uk.png)

![Manual Tab](https://i.imgur.com/xNAQsB0.png)

![Library Tab](https://i.imgur.com/p1thlXJ.png)

![Settings Tab](https://i.imgur.com/cM9LtVl.png)

## How to use
<strong>Automatic:</strong>

1. Download and execute the [NovelGrabber.jar](https://github.com/Flameish/Novel-Grabber/releases/latest) of the latest release
<br>(If you can't execute the jar, try navigating to the jar location with cmd/terminal and use: `java -jar Novel-Grabber.jar`)
2. Enter the link to the novel's Table of Contents page
3. Click on 'Check' to fetch novel info and chapter list
4. Grab chapters

<br>
<strong>Manual:</strong>

Using a table of content:
1. Enter the URL to the novel's "table of contents" page and retrieve all links on the site.
2. Remove all unwanted links from the selection window.
3. Input the chapter container the site uses. 
I strongly suggest using "Auto detect" function, it generally works well and enables grabbing from different sites at once.
<br>You can also specify a CSS selector manually.
4. You can remove unwanted content from the chapter via the "blacklisted tags" window (flag icon at the top).
 <br>Don't forget to set title, author, cover etc.
 
Using Chapter-To-Chapter navigation:
1. Input the URL of your starting point and ending point chapter. (Inclusive)
2. Input the selector for the "Next-Chapter" button. You want to select the `<a>` tag of it. (via css selector)
3. Same as the other step 3.

<br>
<strong>CLI:</strong>

Usage:
[] = optional paramaters 
{} = arguments for paramater

| Parameter | Arguments | Description |
| :--- | :---: | :---|
-gui / `none` | | Starts the Graphical User Interface.
-link | {novel_URL} | URL to the novel's table of contents page. Starts download.
[-wait] | {miliseconds} | Time between each chapter grab.
[-headless] | {chrome/firefox/opera/edge/IE} | Visit the website in your browser. Executes javascript etc.
[-chapters] | {all}, {5 27}, {12 last}	| Specify which chapters to download.
[-path] | {directory_path} | Output directory for the EPUB.
[-account] | {username password} | Add a new account for the host.
[-login] | | Log in on website with saved account. -account is not needed if an account for this domain was added previously.
[-noDesc] | | Don't create a description page.
[-removeStyle] | | Remove all styling from chapter body.
[-getImages] | | Grab images from chapter body as well.
[-displayTitle]| | Write the chapter title at the top of each chapter text.
[-invertOrder] | | Invert the chapter order.
-help | | Shows the help page.

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
 * [webdrivermanager](https://github.com/bonigarcia/webdrivermanager)
 * [selenium ](https://selenium.dev/)
 * [Simple Java Mail ](https://github.com/bbottema/simple-java-mail/)
 
