if pgrep -f Crawling ; then
    echo "Crawling is already running."
    exit 1
else
    java -cp ./tomcat/webapps/is-project/WEB-INF/classes/:./tomcat/webapps/is-project/WEB-INF/lib/* Crawling --max-depth 5 --max-doc 1000 --number-threads 5
fi