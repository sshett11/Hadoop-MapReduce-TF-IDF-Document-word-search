//801021051 SURAJ M SHETTY - Task 4 (accepts as input a user query and outputs a list of documents with scores that best matches the query (search hits))
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

public class Search extends Configured implements Tool {

    private static final Logger LOG = Logger.getLogger(Search.class);
    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Search(), args);
        System.exit(res);
    }

    public int run(String[] args) throws Exception {

        Job job = Job.getInstance(getConf(), " search "); //Job Instance created
        if(args.length != 3){ //no more than 3 arguments to be received so a if statement
            System.exit(1);
        }
        String queryWord = args[2]; //argument 2 to take the search query
        String[] searchQuery = queryWord.trim().toLowerCase().split(" ");
        job.getConfiguration().setStrings("totalargs",searchQuery);
        job.setJarByClass(this.getClass());
        FileInputFormat.addInputPaths(job, args[0]); //TFIDF programs output to be supplied here as input
        FileOutputFormat.setOutputPath(job, new Path(args[1])); // HDFS output fake path
        job.setMapperClass(Map.class); // Mapper
        job.setReducerClass(Reduce.class); // Reducer
        job.setOutputKeyClass(Text.class); //key value pair creation
        job.setOutputValueClass(Text.class);
        return job.waitForCompletion(true) ? 0 : 1; //wait for job to be completed with return statistics
    }

    public static class Map extends
            Mapper<LongWritable, Text, Text, Text> {
        private Text word = new Text();


        List<String> savedquery;
        @Override
        public void setup(Context context) throws IOException, InterruptedException{
            String[] totalargs = context.getConfiguration().getStrings("totalargs");
            savedquery = new ArrayList<String>();
            savedquery =   Arrays.asList(totalargs);

        }

        public void map(LongWritable offset, Text lineText, Context context)
                throws IOException, InterruptedException {

            String hashDelimiter = "#####";
            String line = lineText.toString();
            Text filename = new Text();
            Text tfidf = new Text();
            if (line.isEmpty())
            {
                return;
            }
            String[] eachline = line.toString().split(hashDelimiter); // use hash as delimiter to split
            LOG.info("Each Line: "+eachline[0]); //To debug error

            if( savedquery.contains(eachline[0])){

                String[] filetfidf = eachline[1].split("\\s+"); // tfidf score and its matching name is finded
                filename = new Text(filetfidf[0]);
                tfidf = new Text(filetfidf[1]);
                context.write(filename, tfidf);

            }
        }
    }

    public static class Reduce extends
            Reducer<Text, Text, Text, Text> {
        @Override
        public void reduce(Text word, Iterable<Text> counts,
                           Context context) throws IOException, InterruptedException {
            double sum = 0;
            for (Text count : counts)
            {
                sum += Double.parseDouble(count.toString()); // final tfidf score is calculated
            }
            Text score = new Text(sum + "");
            context.write(word, score);
        }
    }

}