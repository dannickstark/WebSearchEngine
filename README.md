# group-01

<div align= >

# <img align=center width=50px height=50px src="https://media0.giphy.com/media/CVrs76nnBvU7azfTLj/giphy.gif?cid=ecf05e47om1y68g5als66xi5mn32ui6gk2g8wpnv145ag265&rid=giphy.gif&ct=s"> Rapide

</div>
<p align="center">
   <img align="center" height="250px"  src="./repos/screenshot.png" alt="screenshot">
</p>

<p align="center"> 
    <br> 
</p>

## <img align= center width=50px height=50px src="https://thumbs.gfycat.com/HeftyDescriptiveChimneyswift-size_restricted.gif"> Table of Contents

- <a href ="#about"> 📙 Overview</a>
- <a href ="#Started"> 💻 Get Started</a>
- <a href ="#Work"> 🧱 Search Engine Modules </a>
- <a href ="#Video"> 📽 Demo</a>
<hr style="background-color: #4b4c60"></hr>

<a id = "about"></a>

## <img align="center"  height =50px src="https://user-images.githubusercontent.com/71986226/154076110-1233d7a8-92c2-4d79-82c1-30e278aa518a.gif"> Overview

<ul>
<li> The aim of this project is to develop a simple Crawler- based search engine that demonstrates the main features of a search engine
and the interaction between them.</li>
<li> The main features of a search engine</li>
<ul>
<li> Web Crawling</li>
<li> Indexing</li> 
<li> Ranking</li>
</ul>
<br>
<li> Built using <a href="https://en.wikipedia.org/wiki/Java_(programming_language)">Java lnaguage</a>.</li>
<li>  Web interface  for  Search Engine  using <a href="https://en.wikipedia.org/wiki/HTML">Html</a> & <a href="https://en.wikipedia.org/wiki/CSS">CSS</a> & <a href="https://en.wikipedia.org/wiki/JavaScript">JS</a>.</li>

<li> Built using <a href="https://en.wikipedia.org/wiki/PostgreSQL">Postgresql</a>.</li>
</ul>
<hr style="background-color: #4b4c60"></hr>
<a id = "Started"></a>

## <img  align= center width=50px height=50px src="https://c.tenor.com/HgX89Yku5V4AAAAi/to-the-moon.gif"> Get Started

<ol>
<li>Clone the repository.

<br>

```
git clone https://git.cs.uni-kl.de/dbis/is-project-22/group-01.git
```

</li>
<li> You will need to download and install <a href="https://www.oracle.com/java/technologies/downloads/">Jdk</a>. </li>

<li> You will need to download <a href="https://tomcat.apache.org/download-10.cgi">Tomcat</a>. </li>

<li> You will need to download and install <a href="https://www.pgadmin.org/download/">pgAdmin</a>. 
  <ul>
    <li>Note the default port you choosed</li>
  </ul>
</li>

<li>Open the cloned folder with IntelliJ</li>

<li>Use Gradle to install all the dependencies</li>

<li>
  Create a database in pgAdmin
  <ul>
    <li>Note the name and the password you defined</li>
  </ul>
</li>

<li>Go to IntelliJ and edit the file
<br>

```
/src/main/java/DB/DBVars.java
```
</li>

<li>
Do the migration. It will Create all the tables.
<br>Go to

```
/src/main/java/DB/Migrator.java
```
and run it.
</li>

<li>To run the Crawler, goto the following file and run it

```
/src/main/java/Crawling.java
```
</li>
</ol>

<br>
<hr style="background-color: #4b4c60"></hr>

## How to run the CLI
Run:

```
/src/main/java/CLI.java
```
<br>

<br>
<hr style="background-color: #4b4c60"></hr>

## How to run the Tomcat Server
Use the following <a href="https://www.jetbrains.com/help/idea/run-debug-configuration-tomcat-server.html">instructions</a> for it.

<br>
<hr style="background-color: #4b4c60"></hr>
<a id = "Work"></a>

## <img align= center width=65px height=65px src="https://raw.githubusercontent.com/EslamAsHhraf/EslamAsHhraf/main/images/skills.gif"> Search Engine Modules

<table align="left;">
<tr>
<th width=23%>Module</th>
<th>Description</th>
</tr>
<tr>
<td> 🔷 Web Crawler</td>
<td>A web crawler is a software agent that collects web documents. The crawler begins with a list of URLs (seed set). It retrieves the documents identified by these URLs and extracts hypertext links from them. The URLs that have been extracted are added to the list of URLs to be downloaded. Web crawling is thus a recursive process.</td>
</tr>
<tr>
<td>🔶 Indexer</td>
<td>The output of web crawling process is a set of downloaded HTML documents. To respond to user queries fast enough, the contents of these documents have to be indexed in a data structure that stores the words contained in each document and their importance.</td>
</tr>
<tr>
<td> 🔷 Query Processor</td>
<td>This module receives search queries, performs necessary preprocessing and searches the index for relevant documents. Retrieve documents containing words that share the same stem with those in the search query. For example, the search query “travel” should match (with lower degree) the words “traveler”, “traveling” … etc.</td>
</tr>
<tr>
<td>🔶 Phrase Searching</td>
<td>Search engines will generally search for words as phrases when quotation marks are placed around the phrase.</td>
</tr>
<tr>
<td>🔷 Ranker</td>
<td>
<p>The ranker module sorts documents based on their popularity and relevance to the search query.
</p>
<ol>
<li>Relevance</li>
<p>Relevance is a relation between the query words and the result page and could be calculated in several ways such as tf-idf of the query word in the result page or simply whether the query word appeared in the title, heading, or body. And then you aggregate the scores from all query words to produce the final page relevance score.</p>
<li>Popularity</li>
<p>Popularity is a measure for the importance of any web page regardless the requested query. You can use pagerank algorithm (as explained in the lecture) or other ranking algorithms to calculate each page popularity.</p>
</ol>
</td>
</tr>
<td>🔷 Web Interface</td>
<td><p>We  implement a web interface for  search engine.</p> 
<ul>
<li>This interface receives user queries and displays the resulting pages returned by the engine</li>
<br>
<li>The result appears with snippets of the text containing queries words. The output looks like google's results page.</li>
</ul>
</td>
</tr>
</table>

<hr style="background-color: #4b4c60"></hr>
<a id ="Video"></a>

## <img  align= center width= 70px height =70px src="https://img.genial.ly/5f91608064ad990c6ee12237/bd7195a3-a8bb-494b-8a6d-af48dd4deb4b.gif?genial&1643587200063"> Demo

<div  align="center">
  <img align="center" height=370px  src="./repos/demo.gif">
</div>