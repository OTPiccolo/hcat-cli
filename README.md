# Haplotype Comparison and Analysis Tool (HCAT)
## Command Line Interface (CLI)

### Overview
HCAT is a tool to help all scientists out there to analyze their sequence data faster and more reliable, by utilizing the strengths of a computer to automate repetitive tasks in a uniform way. No longer do you have to waste hours painstakingly categorizing sequence data into haplotypes, always fearing that you might slip up somewhere and by thus invalidating all the hard work you did.

With this tool, you can read in sequence data from the command line and it automatically categorizes them into haplotypes, and compares those haplotypes to a master sequence, displaying all positions that differ.

It can read in sequences in different formats, like...
* Fasta
* Phylip (normal and TCS)
* CSV (Comma Separated Value)

### Examples
All references to **hcat.jar** mean the actual file name of the hcat jar you downloaded.

This line will read in the sequences from the given file in the same folder as *hcat.jar*, using the first found sequence as the master sequence and write out the result on the command line.
```
java -jar hcat.jar -haplotype sequencefile.txt
```  

This line will read in the sequences from the given file, write the result to the other given file and use the sequence with the given ID as the master sequence.
```
java -jar hcat.jar -haplotype -i inputfile.fas -o outputfile.txt -mi "ID of sequence"
```

This line will write out help information about hcat and all supported options.
```
java -jar hcat.jar -haplotype -?
```