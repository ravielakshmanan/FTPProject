JFLAGS = -g
JC = javac

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	client/client.java \
	server/server.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) client/*.class server/*.class
