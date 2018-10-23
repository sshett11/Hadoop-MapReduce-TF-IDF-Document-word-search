INFO - We will use Hadoop MapReduce to compute the elements in this project. Information retrieval (IR) is concerned with finding material (e.g., documents) of an unstructured nature (usually text) in response to an information need (e.g., a query) from large collections. One approach to identify relevant documents is to compute scores based on the matches between terms in the query and terms in the documents. For example, a document with words such as ball, team, score, championship is likely to be about sports. It is helpful to define a weight for each term in a document that can be meaningful for computing such as score. So we will find Document word count, term frequency, inverse document frequency, and their product, term frequency-inverse document frequency (TF-IDF) & a Search file (accepts as input a user query and outputs a list of documents with scores that best matches the query (a.k.a search hits))

LANGUAGE - JAVA

OS - Linux Mint (Hadoop and Java installed locally & all Hadoop Jar files and environment variables like  HADOOP_HOME is set in the IDE).

JDK USED - jdk1.8.0_151

IDE USED FOR PROJECT - IntelliJ IDEA Ultimate Edition

HOW TO RUN ON IntelliJ on Linux - 
1. For (DocWordCount.java) Run/Debug Configuration
Program arguments - cantrbry output

2. For (TermFrequency.java) Run/Debug Configuration
Program arguments - cantrbry output2

3. For (TFIDF.java) Run/Debug Configuration
Program arguments - cantrbry output3

4. For (Search.java) Run/Debug Configuration [Note - TFIDF.java's output is it's input]
Program arguments - /output3 output4 "computer science"   

-----------------------------------------------------------------------------------------------------------------------------

HOW TO RUN ON CLOUDER VM  -  
**To Leave from Cloudera machine safe mode (if present) -
sudo -u hdfs hdfs dfsadmin -safemode leave

1>>> DocWordCount program
To Delete Folder with files - 
hadoop fs -rm -r  /user/cloudera/DocWordCount

To Create folders -
hadoop fs -mkdir /user/cloudera/DocWordCount /user/cloudera/DocWordCount/input

To Send Files from local storage to hdfs - 
hadoop fs -put /home/cloudera/cantrbry /user/cloudera/DocWordCount/input

To Compile and Create Class file - 
mkdir -p build
javac -cp /usr/lib/hadoop/*:/usr/lib/hadoop-mapreduce/* DocWordCount.java -d build -Xlint

To Create JAR - 
jar -cvf DocWordCount.jar -C build/ .

To Execute JAR File passing input location for files and output path -
hadoop jar DocWordCount.jar org.myorg.DocWordCount /user/cloudera/DocWordCount/input/cantrbry /user/cloudera/DocWordCount/output

To read all output -
hadoop fs -cat /user/cloudera/DocWordCount/output/*

To send output to file (over write if exists) -
hadoop fs -cat /user/cloudera/DocWordCount/output/* > DocWordCount.out

-----------------------------------------------------------------------------------------------------------------------------

2>>> TermFrequency program
To Delete Folder with files - 
hadoop fs -rm -r  /user/cloudera/TermFrequency

To Create folders -
hadoop fs -mkdir /user/cloudera/TermFrequency /user/cloudera/TermFrequency/input

To Send Files from local storage to hdfs - 
hadoop fs -put /home/cloudera/cantrbry /user/cloudera/TermFrequency/input

To Compile and Create Class file - 
mkdir -p build
javac -cp /usr/lib/hadoop/*:/usr/lib/hadoop-mapreduce/* TermFrequency.java -d build -Xlint

To Create JAR - 
jar -cvf TermFrequency.jar -C build/ .

To Execute JAR File passing input location for files and output path -
hadoop jar TermFrequency.jar org.myorg.TermFrequency /user/cloudera/TermFrequency/input/cantrbry /user/cloudera/TermFrequency/output

To read all output -
hadoop fs -cat /user/cloudera/TermFrequency/output/*

To send output to file (over write if exists) -
hadoop fs -cat /user/cloudera/TermFrequency/output/* > TermFrequency.out


-----------------------------------------------------------------------------------------------------------------------------

3>>> TFIDF program (PASS TermFrequency.java while compiling) as given below -
To Delete Folder with files - 
hadoop fs -rm -r  /user/cloudera/TFIDF

To Create folders -
hadoop fs -mkdir /user/cloudera/TFIDF /user/cloudera/TFIDF/input

To Send Files from local storage to hdfs - 
hadoop fs -put /home/cloudera/cantrbry /user/cloudera/TFIDF/input

To Compile and Create Class file - 
mkdir -p build
javac -cp /usr/lib/hadoop/*:/usr/lib/hadoop-mapreduce/* TermFrequency.java TFIDF.java -d build -Xlint

To Create JAR - 
jar -cvf TFIDF.jar -C build/ .

To Execute JAR File passing input location for files and output path -
hadoop jar TFIDF.jar org.myorg.TFIDF /user/cloudera/TFIDF/input/cantrbry /user/cloudera/TFIDF/output

To read all output -
hadoop fs -cat /user/cloudera/TFIDF/output/*

To send output to file (over write if exists) -
hadoop fs -cat /user/cloudera/TFIDF/output/* > TFIDF.out


-----------------------------------------------------------------------------------------------------------------------------

4>>> Search program (PASS TFIDF.java's(3rd program's) Output path as input while compiling) as given below -
To Delete Folder with files - 
hadoop fs -rm -r  /user/cloudera/Search

To Create folders -
hadoop fs -mkdir /user/cloudera/Search /user/cloudera/Search/input

To Compile and Create Class file - 
mkdir -p build
javac -cp /usr/lib/hadoop/*:/usr/lib/hadoop-mapreduce/* Search.java -d build -Xlint

To Create JAR - 
jar -cvf Search.jar -C build/ .

To Execute JAR File passing input location for files and output path -
hadoop jar Search.jar org.myorg.Search /user/cloudera/TFIDF/output /user/cloudera/Search/output "computer science"
hadoop jar Search.jar org.myorg.Search /user/cloudera/TFIDF/output /user/cloudera/Search/output1 "data analysis"

To read all output -
hadoop fs -cat /user/cloudera/Search/output/*
hadoop fs -cat /user/cloudera/Search/output1/*

To send output to file (over write if exists) -
hadoop fs -cat /user/cloudera/Search/output/* > query1.out
hadoop fs -cat /user/cloudera/Search/output1/* > query2.out

