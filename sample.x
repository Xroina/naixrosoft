  print("hello, world. こんにちは\n");
  a = -5;
  b = 200;
  print "" + (a + b * 10) + "\n";
  print "" + ((a + b) * 10) + "\n";

print "" + :sub:function(b, a) + "\n"

  i = 0;
  while (i < 5) {
    print(i);
    if (i <= 2) {
      print("i <= 2\n");
    } else {
      print("i > 2\n");
    }
    i = i + 1;
  }
  print "goto c\n"
  goto *c

*b
  print("b\n");
  goto *d

*c
  print "cls\n"
  cls
  print("c\n");
  goto *b;

*d
  print("d\n");

  gosub *sub;
  print("returned.\n");

  locate 20, 10
  print "locate 20, 10\n"

	for i = 0; i < 10; i++ {
		x = abs(rnd) % 80
		y = abs(rnd) % 30
		locate x, y
		print "A" + x + "," + y + "\n"
	}

	print "\n" + rnd + "\n"
  return;

*sub
  print("hello,こんにちは\n");
  print("\"subroutine\"\n");
  print "" + (10 + a * b) + "\n";
  return;
