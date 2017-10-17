void setup() {
  int seed = 0; // TRY DIFFERENT SEED VALUES

  boolean found_the_needle = false; // assume you won't find the number     
  long t_sum = 0;
  int t_count = 10; // increase for greater accuracy at a trade off of time

  println("Search haystack " + t_count + " times.");

  for (int i=0; i<t_count; i++) {
    int haystack[] = generateHaystack(10000000, seed);
    //    println("This is the Haystack Unsorted ");
    //    println(haystack);
    long t = System.nanoTime();



    //BubbleSortHaystack(haystack);
    //CountSortHaystack(haystack);
    quickSort(haystack);


    found_the_needle = searchHaystack(42, haystack); // deep philosophical search...
    long del = System.nanoTime() - t;
    println("Done searching in " + str(del/1000) + " microseconds.");
    if (i >= 3) // the earlier times are sometimes corrupted by memory mamangement and system processes
      t_sum += del;
  }
  long t_average = t_sum/(t_count-3);

  println("Average of last " + str(t_count - 3) + " searches is " + str(t_average/1000) + " microseconds.");

  if (found_the_needle) {
    println("\nThe needle is in there!");
  } else {
    println("\nNo trace of the needle could be found.");
  }

  exit();
}

int[] generateHaystack(int size, int seed) {
  int[] numbers = new int[size];

  randomSeed(seed);
  for (int i=0; i<size; i++) {
    numbers[i] = int(random(0, size)); // needles
  }

  return(numbers);
}

void BubbleSortHaystack(int[] haystack) {
  int times = haystack.length; //how many times it repeates 
  for (int j = times; j >=0; j--) { //do the following for all the values in the haystack
    for (int i = 0; i<times-1; i++) {
      if (haystack[i] > haystack[i+1]) {
        int temp = haystack[i]; // store heighter value in temp variable
        haystack[i] = haystack[i+1]; //since the first number is greater than the second, switch them around
        haystack[i+1] = temp; //compleating the action stated above
      }
    }
  }
}
void CountSortHaystack(int[] haystack) {
  int[] buckets = new int[haystack.length];
  int count = 0;
  for (int i = 0; i < haystack.length; i++) {
    buckets[haystack[i]]++; //increase the value of the bucket at the haystack value
  }
  for (int i = 0; i < haystack.length; i++) { //converts buckets back into the sorted haystack 
    for (int j = 0; j < buckets[i]; j ++) {
      haystack[count] = i; //set the haystack at count equil to the bucket number and repeate till bucket is empty
      count++; //variable to tell how many integers have been sorted
    }
  }
  //  println("this is the sorted Haystack "); 
  //  println(haystack);
}
void quickSort(int haystack[], int leftIndex, int rightIndex) {

  int start = leftIndex;
  int end = rightIndex;

  if (end - start >= 1) {

    int pivot = haystack[leftIndex];

    while (end > start) {
      while (haystack[start] <= pivot && start <= rightIndex && end > start) {
        start ++;
      }
      while (haystack[end] > pivot && end >= leftIndex && end >= start) 
        end --;
      
      if (end > start) {
        swap(haystack, start, end);
      }
    }
    swap(haystack, leftIndex, end);

    quickSort(haystack, leftIndex, end - 1);
    quickSort(haystack, end + 1, rightIndex);
  }
  else {
    return;
  }
}
boolean searchHaystack(int needle, int[] haystack) {
  int min = 0;
  int max = haystack.length-1;
  while (min <= max) {
    int mid = (max+min)/2; //find middle value and compares it
    if (needle < haystack[mid]) { //compare values to decide where to go
      max = mid - 1; 
    } else if (needle > haystack[mid]) { //same as above
      min = mid + 1; 
    } else if (needle == haystack[mid]) { //returns true if the needle is in there
      return true;
    }
  }
  return false; //otherwise it returns false by defult 
}

