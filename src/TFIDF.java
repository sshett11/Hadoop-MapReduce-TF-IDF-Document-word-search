//801021051 SURAJ M SHETTY - Task 3 (job takes the output files of the first job(TermFrequency()) as input and computes TF-IDF values)
import java.io.IOException;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import org.apache.hadoop.fs.ContentSummary;


public class TFIDF extends Configured implements Tool {

    private static final Logger LOG = Logger.getLogger(TFIDF.class);


    public static void main(String[] args) throws Exception {

        String[] argstemp = new String[args.length];
        for(int i=0;i<args.length;i++)
        {
            argstemp[i] = args[i];
        }
        argstemp[1] = argstemp[0]+"temp";   //temp folder is added to save the output of TF
        System.out.println("MapReduce Job 1 - TF");
        int res1 = ToolRunner.run(new TermFrequency(), argstemp); //(TermFrequency() calls the TermFrequency ava file
        System.out.println("MapReduce Job 2 - TF/IDF");
        int res = ToolRunner.run(new TFIDF(), args);
        System.exit(res);
    }

    public int run(String[] args) throws Exception {
        FileSystem fs = FileSystem.get(new Configuration());
        ContentSummary summary = fs.getContentSummary(new Path(args[0]));
        getConf().set("Files", summary.getFileCount() + ""); //Files in input folder to be checked
        Job job = Job.getInstance(getConf(), " tfidf ");
        job.setJarByClass(this.getClass());
        FileInputFormat.addInputPaths(job, args[0] + "temp"); //TF's output will be input
        FileOutputFormat.setOutputPath(job, new Path(args[1])); // HDFS output
        job.setMapperClass(Map.class); // Mapper
        job.setReducerClass(Reduce.class); // Reducer
        job.setOutputKeyClass(Text.class); //key value pair creation
        job.setOutputValueClass(Text.class);
        return job.waitForCompletion(true) ? 0 : 1; //wait for job to be completed with return statistics
    }


    public static class Map extends
            Mapper<LongWritable, Text, Text, Text> {

        private Text word = new Text();
        public void map(LongWritable offset, Text lineText, Context context)
                throws IOException, InterruptedException {

            String line = lineText.toString();
            String[] delimiterSplittedArray = line.split("#####");// delimiter -'####' to seperate the currentword
            Text word  = new Text(delimiterSplittedArray[0].trim().toLowerCase()); //to lowercase if any uppercase
            String value = delimiterSplittedArray[1];
            String fv = value.split("\\s+")[0]+"="+value.split("\\s+")[1]; //joins the file name and counts of = sign
            Text finaltext = new Text(fv);
            context.write(word,finaltext);
        }
    }

    public static class Reduce extends
            Reducer<Text, Text, Text, Text> {

        @Override
        public void reduce(Text word, Iterable<Text> count,
                           Context context) throws IOException, InterruptedException {
            long Files = context.getConfiguration().getLong("Files", 1);  //Using hadoop config to get the file count
            ArrayList<Text> wordfiles = new ArrayList<Text>();
            for (Text filecount : count) {
                wordfiles.add(new Text(filecount.toString()));
            }

            for (Text files : wordfiles) {
                String[] tffile = files.toString().split("="); //filename and tf are seperated by =
                double tfidf = 0;
                tfidf = Double.parseDouble(tffile[1])
                        * Math.log10(1 + ((1.0*Files) / wordfiles.size())); //TF-IDF calculation
                String combined = word.toString() + "#####" + tffile[0] + "\t";
                Text fword = new Text(combined);
                context.write(fword, new Text(tfidf + ""));
            }
        }
    }


}