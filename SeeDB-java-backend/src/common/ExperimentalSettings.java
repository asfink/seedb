package common;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * This class contains the settings for each run of SeeDB. It is mainly used for
 * experimentation, in other cases, supplied default instance is used
 * @author manasi
 *
 */
public class ExperimentalSettings {
	public enum ComparisonType {TWO_DATASETS, ONE_DATASET_FULL, 
		ONE_DATASET_DIFF, MANUAL_VIEW}; 
	public ComparisonType comparisonType = ComparisonType.ONE_DATASET_FULL; // Default SeeDB q vs. entire data
	
	public enum DifferenceOperators {AGGREGATE, CARDINALITY, DATA_SAMPLE}; //ALL, A_B_TESTING, CLASSIFICATION, 
	public List<DifferenceOperators> differenceOperators; 	// ignore for basic tests	
	public int rowSampleSize = 10; 							// ignore for basic tests

	public boolean noAggregateQueryOptimization = false; 	// true = no optimizations
	public boolean optimizeAll = true; 						// true = apply all optimizations
	
	public boolean combineMultipleAggregates = true;		// OPT1: true = multiple measures together
	public int maxAggSize = 2;								// OPT1: combine aggregates UP to maxAggSize
	
	public boolean combineMultipleGroupBys = true;			// OPT2: combine group-bys together in one query
	public int maxGroupBySize = 2;							
	public boolean useBinPacking = false;					// OPT2: ignore for basic tests, not implemented
	public boolean useHeuristic = false;					// OPT2: ignore for basic tests [huffman]
	

	public boolean mergeQueries = true;						// OPT3: combine comparison and target q
	
	public boolean useParallelExecution = false;			// OPT4: parallel execution
	public int maxDBConnections = 40;						// OPT4: actual is double; max POSTGRES = 100
															// OPT4: do not set above 45	

	public int groupBySize = 1; 							// ignore for basic tests 
															// number of group bys in the final views. 1 since we want 1D
	
	public boolean useTempTables = true;					// set this

	public String logFile = null;							// log file for profiling, set if you want
															// else goes to stdout
															// process using processTestOutput.py in scripts
	
	//public String shared_buff="32MB";						

	
	/**
	 * Get default settings for SeeDB
	 * @return settings object
	 */
	public static ExperimentalSettings getDefault() {
		ExperimentalSettings settings = new ExperimentalSettings();
		settings.differenceOperators = Lists.newArrayList(DifferenceOperators.DATA_SAMPLE, DifferenceOperators.AGGREGATE, DifferenceOperators.CARDINALITY);
		return settings;
	}
	
	// returns a description of the settings used to run the SeeDB test
	public String getDescriptor() {
		List<String> l = Lists.newArrayList();
		l.add("istc");
		l.add("final");
		l.add(useParallelExecution ? "parallel_" + this.maxDBConnections : "seq");
		if (noAggregateQueryOptimization) {
			l.add("NoOp");
		}
		if (optimizeAll) {
			l.add("ALL");
			l.add(maxGroupBySize + "GB");
			l.add(maxAggSize + "AGG");
		}
		if (!optimizeAll && !noAggregateQueryOptimization) {
			if (combineMultipleAggregates) {
				l.add("MultipleAgg");
				l.add(maxAggSize + "AGG");
			}
			if (combineMultipleGroupBys) {
				l.add("MultipleGB");
				l.add(maxGroupBySize + "GB");
			}
			if (mergeQueries) {
				l.add("Merged");
			}
		}
		if (useTempTables) {
			l.add("TempTables");
		}
		return Joiner.on("_").join(l);
	}
}
