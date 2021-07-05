# Haplotype Comparison and Analysis Tool (HCAT)
## Command Line Interface (CLI)

### Overview
HCAT is a tool to help all scientists out there to analyze their haplotype sequence data faster and more reliable, by utilizing the strengths of a computer to automate repetitive tasks in a uniform way. No longer do you have to waste hours painstakingly categorizing sequence data into haplotypes, always fearing that you might slip up somewhere and thus invalidate all the hard work you did.

### Conversion
HCAT can convert sequence data between different formats, to easily import them into different tools that might only read a specific file format.

#### Supported file formats for sequences
* Fasta
* Phylip (normal and for TCS (Clement et al., 2000))
* CSV (Comma Separated Value)

### Haplotype Analysis
Analyze sequence data and categorize it into haplotypes. Will also compare those haplotypes with a reference sequence, displaying all differences.

### Codon Translation
Translates sequences into their codon equivalent. The codon translation data is already configured, so you just have to know the correct codon number you want to use. Please see https://www.ncbi.nlm.nih.gov/Taxonomy/Utils/wprintgc.cgi for the correct codon number to use. The data HCAT uses is also supplied by them.

## Examples
All references to *hcat.jar* mean the actual file name of the hcat jar you downloaded. If no full file name for the input or output data is specified, the files will also be expected to be in the same folder as *hcat.jar*.

This line will write out help information about hcat and all supported options.
```
java -jar hcat.jar -?
```

### Conversion
This line will read in the sequences as Fasta format (determined by file ending) and output them as Phylip TCS format on the command line.
```
java -jar hcat.jar -conversion sequencefile.fas tcs
```

This line will read in the sequences as Phylip TCS format and output them as Fasta format to the given output file.
```
java -jar hcat.jar -conversion -i inputfile.txt -o outputfile.txt -f tcs -of fasta
```

### Haplotype Analsysis
This line will read in the sequences, using the first found sequence as the master sequence and write out the result on the command line.
```
java -jar hcat.jar -haplotype sequencefile.fas
```

This line will read in the sequences from the given file, write the result to the other given file and use the sequence with the given ID as the master sequence.
```
java -jar hcat.jar -haplotype -i inputfile.fas -o outputfile.txt -mi "ID of sequence"
```

### Codon Translation
This line will read in the sequences, and uses the *The Standard Code* (number 1) codon table to translate them, writing them to the command line.
```
java -jar hcat.jar -codon 1 sequencefile.fas
```

This line will read in the sequences from the given file, translate them using *The Echinoderm and Flatworm Mitochondrial Code* (number 9) and write them to the given output file.
```
java -jar hcat.jar -codon -i inputfile.fas -o outputfile.fas -cn 9
```
