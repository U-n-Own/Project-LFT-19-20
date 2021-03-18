.class public Output 
.super java/lang/Object

.method public <init>()V
 aload_0
 invokenonvirtual java/lang/Object/<init>()V
 return
.end method

.method public static print(I)V
 .limit stack 2
 getstatic java/lang/System/out Ljava/io/PrintStream;
 iload_0 
 invokestatic java/lang/Integer/toString(I)Ljava/lang/String;
 invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
 return
.end method

.method public static read()I
 .limit stack 3
 new java/util/Scanner
 dup
 getstatic java/lang/System/in Ljava/io/InputStream;
 invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V
 invokevirtual java/util/Scanner/next()Ljava/lang/String;
 invokestatic java/lang/Integer.parseInt(Ljava/lang/String;)I
 ireturn
.end method

.method public static run()V
 .limit stack 1024
 .limit locals 256
 invokestatic Output/read()I
 istore 0
L3:
 invokestatic Output/read()I
 istore 1
L4:
L6:
 iload 0
 iload 1
 if_icmpne L7
 goto L5
L7:
 iload 0
 iload 1
 if_icmpgt L9
 goto L10
L9:
 iload 0
 iload 1
 isub 
 istore 0
L11:
 goto L8
L10:
 iload 1
 iload 0
 isub 
 istore 1
L12:
L8:
 goto L6
L5:
 iload 0
 invokestatic Output/print(I)V
L13:
 iload 0
 iload 1
 invokestatic Output/print(I)V
 ldc 1000
 ldc 1
 iadd 
 ldc 1
 iadd 
 invokestatic Output/print(I)V
 invokestatic Output/print(I)V
L14:
L2:
L1:
L0:
 return
.end method

.method public static main([Ljava/lang/String;)V
 invokestatic Output/run()V
 return
.end method

