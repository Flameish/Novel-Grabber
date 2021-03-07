# ![alt text](https://i.imgur.com/LrV2tLe.png)Novel-Grabber
Novel-Grabber is a gui based web scrapper that can download and convert chapters into EPUB from various supported web/light novel sites or from any other site manually.

## Features
- Extensive and easy to use manual grabbing
- Image support
- Style removal
- Unwanted content removal via blacklisted HTML tags
- EPUB metadata
- Desktop and email notifications for new chapter releases of followed novels
- Automatic chapter body detection 
- Headless browser support
- Login support for certain host sites
- CLI 
- Telegram Bot: [@NovelGrabbyBot](http://t.me/NovelGrabbyBot)

## How to use
<strong>Automatic:</strong>

1. Download [Novel-Grabber.zip](https://github.com/Flameish/Novel-Grabber/releases/latest/download/Novel-Grabber.zip) and execute the launcher inside
<br>(If you can't execute the jar, try to start it via the terminal command: `java -jar NG-Launcher.jar`)
2. Enter the link to the novel's Table of Contents page
3. Click on 'Check' to fetch novel info and chapter list
4. Grab chapters

<br>
<strong>Manual:</strong>

Using a table of content:
1. Enter the URL to the novel's "table of contents" page and retrieve all links on the site.
2. Remove all unwanted links from the selection window.
3. Input the chapter container the site uses. 
I strongly suggest using the "auto detect" function, it generally works well and enables grabbing from different sites.
<br>Or you can specify a CSS selector manually. [Guide](https://github.com/Flameish/Novel-Grabber/issues/62#issuecomment-730305855)
4. You can remove unwanted content from the chapter via the "blacklisted tags" window (flag icon at the top).
 <br>Don't forget to set title, author, cover etc.
 
Using Chapter-To-Chapter navigation:
1. Input the URL of your starting point and ending point chapter. (Inclusive)
2. Input the selector for the "Next-Chapter" button. You want to select the `<a>` tag of it. (via css selector)

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
[-headless] | {chrome/firefox/opera/edge/IE/headless} | Visit the website in your browser. Executes javascript etc.
[-chapters] | {all}, {5 27}, {12 last}	| Specify which chapters to download.
[-path] | {directory_path} | Output directory for the EPUB.
[-login] | | Log in on website with saved account. -account is not needed if an account for this domain was added previously.
[-noDesc] | | Don't create a description page.
[-removeStyle] | | Remove all styling from chapter body.
[-getImages] | | Grab images from chapter body as well.
[-displayTitle]| | Write the chapter title at the top of each chapter text.
[-invertOrder] | | Invert the chapter order.
-help | | Shows the help page.

<br>
<strong>Telegram bot:</strong>

To host your own Telegram bot you need to add the line `telegramApiToken=your_token ` to `config.ini`

By default the bot will read the output for `/info` and `/sources` from files inside the telegram folder. 
The files need to be named `info.txt` and `supported_Sources.txt` respectively.

To start the bot you need to use the `-telegramBot` parameter: `java -jar Novel-Grabber.jar -telegramBot`

## Screenshots

![Automatic Tab](https://i.imgur.com/xBUdkmL.png)

![Manual Tab](https://i.imgur.com/jDm2ABW.png)

![Library Tab](https://i.imgur.com/ef5P4mf.png)

## Natively supported sites
- [101novel](https://www.101novel.com/)
- [17k](https://www.17k.com/)
- [9kqw](https://9kqw.com/)
- [AlphaPolis](https://www.alphapolis.co.jp/novel/)
- [Asian Hobbyist](https://www.asianhobbyist.com/)
- [Biquge](http://www.biquge.se/)
- [Blackbox Translations](https://blackbox-tl.com/)
- [Booklat](https://booklat.com.ph)
- [booknet](https://booknet.com/)
- [BoxNovel.com](https://boxnovel.com)
- [BoxNovel.net](https://boxnovel.net)
- [BoxNovel.org](https://boxnovel.org/)
- [Chicken Gege](https://www.chickengege.org/)
- [Chrysanthemum Garden](https://chrysanthemumgarden.com)
- [Comrade Mao](https://comrademao.com)
- [Creative Novels](https://creativenovels.com)
- [Dao Novel](https://daonovel.com/)
- [Dreame](https://dreame.com)
- [Dummy Novels](https://dummynovels.com/)
- [Exiled Rebels Scanlations](https://exiledrebelsscanlations.com/)
- [FanFiction](https://fanfiction.net)
- [FanFiktion](https://fanfiktion.de)
- [FastNovel](https://fastnovel.net/)
- [FicFun](https://ficfun.com)
- [Foxaholic](https://foxaholic.com)
- [Foxteller](https://foxteller.com)
- [fujossy](https://fujossy.jp/)
- [Honeyfeed](https://honeyfeed.fm)
- [Inkitt](https://inkitt.com)
- [ISO Translations](https://isotls.com)
- [jjwxc](https://www.jjwxc.net/)
- [JPMTL](https://jpmtl.com/)
- [LiberSpark](https://liberspark.com)
- [Light Novels Translations](https://lightnovelstranslations.com)
- [Light Novel World](https://www.lightnovelworld.com/)
- [LNMTL](https://lnmtl.com/)
- [Machine-Translation](https://www.machine-translation.org/)
- [MoboReader](https://www.moboreader.net/)
- [MoonQuill](https://moonquill.com)
- [MTLNovel](https://mtlnovel.com)
- [Novel Full](https://novelfull.com)
- [NovelFun](https://novelfun.net/)
- [Novelhall](https://novelhall.com)
- [Novels Online](https://novelsonline.net/)
- [Novelsrock](https://novelsrock.com/)
- [NovelUpdates.cc](https://www.novelupdates.cc/)
- [Novel Updates](https://novelupdates.com)
- [Quotev](https://quotev.com)
- [ReadLightNovel](https://www.readlightnovel.org/)
- [ReadNovelFull.Com](https://readnovelfull.com/)
- [Re:Library](https://re-library.com)
- [Royal Road](https://royalroad.com)
- [Scribble Hub](https://scribblehub.com)
- [shu111](http://shu111.com/)
- [shubaow](https://www.shubaow.net/)
- [Snowy Codex](https://snowycodex.com/)
- [SofaNovel](https://www.sofanovel.com/)
- [Tapas](https://tapas.io)
- [TapRead](https://tapread.com)
- [Truyen Full](https://truyenfull.vn/)
- [UntamedAlley](https://untamedalley.com/)
- [Veratales](https://veratales.com/)
- [VipNovel](https://vipnovel.com/)
- [Volare Novels](https://volarenovels.com)
- [Wattpad](https://wattpad.com)
- [Webnovel](https://webnovel.com)
- [WebTruyen](https://webtruyen.com/)
- [Wenxue](http://wenxue.iqiyi.com/)
- [Wordrain](https://wordrain69.com)
- [WuxiaWorld.co](https://wuxiaworld.co)
- [Wuxiaworld.com](https://wuxiaworld.com)
- [WuxiaWorld.online](https://wuxiaworld.online)
- [WuxiaWorld.site](https://wuxiaworld.site)
- [xiaoshuo](https://www.zhenhunxiaoshuo.com/)

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
 * [Readability4J](https://github.com/dankito/Readability4J)
 * [Notify](https://github.com/dorkbox/Notify)
 * [icons8](https://icons8.com)
 * [FlatLaf](https://www.formdev.com/flatlaf/)
