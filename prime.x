# MAXまでの素数を求める
MAX = 1000
HALF_OF_MAX = MAX / 2
aliquot = 1
prime_flag = 0
number_of_prime = 0

for number = 1; number <= MAX; number++  {
  for(i = 2; i <= number; i++) {
    for c = 1; c <= HALF_OF_MAX; c++ {
      aliquot = number - i * c
      if(aliquot == 0){
        prime_flag++
        if(prime_flag > 1){
          break 2
        }
      }
    }
  }
  if(prime_flag == 1){
    print "" + (number_of_prime + 1) + "個目の素数:" + number + "\n"
    number_of_prime++
  }
  prime_flag = 0
}

print "MAXまでの素数の個数:" + number_of_prime + "\n"
