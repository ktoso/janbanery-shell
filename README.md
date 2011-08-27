Janbanery-Shell - Kanbanery.com board from your favourite shell!
=====================================================================
Kanbanery
---------
<a href="https://kanbanery.com/" style="display:block; background:black;">
<img src="https://sckrk.kanbanery.com/images/kanbanery-logo-small.png?1303131477" alt="[kanbanery]" style="background:black;"/><br/>
</a>

<a href="https://kanbanery.com/">Kanbanery</a> is a web app for managing your projects Kanban Style (somewhat similiar to SCRUM, but less restrictive and strict).
Kanbanery has an web interface, as well as a iPad and iPhone version. The RESTful API is open so anyone can write his
favourite tool to be integrated with it - and that's what I'm doing. This connector will be used in the upcomming Intellij
IDEA plugin as well as the Android version of the app.

Janbanery
---------
**Janbanery** is a **Java library** that provides a **fluent** and **typesafe** (no magic strings etc) API to access 100% of Kanbanery's features.
Well, in fact, even above 100% (sic!) as we've added some filtering and mass operations if you'd want to count those as "atomic from the users perspective".

Feel free to use this library in any project integrating Kanbanery with your app or tool. Also, please share any thoughts about it and request new features where you'd see some possible cool things.

If you have any comments, feel free to open issues or contact me <a href="mailto:konrad.malawski@java.pl">via email</a>.

Janbanery Shell
===============
Janbanery shell is using the **Java Script Engine** and **Groovy** to enable you to use your Kanbanery board, without even taking a look at it, *all from your favourite place - the shell*.

If you want to use **Janbanery-shell**, just download the **<a href="https://github.com/ktoso/janbanery-shell/blob/master/release/uber-janbanery-shell-1.0-SNAPSHOT.jar?raw=true">executable jar</a>** and run it by:

```java
java -jar uber-janbanery-shell-1.0-SNAPSHOT.jar
```

And start using it!
Logging in and how it works

---------------------------
It's easy to login with *Janbanery-Shell*, just enter your username and password. If you entered it right, *the API key for kanbanery will be fetched*,
and used thoughout all requests to kanbanery - to keep your password safe!

Also, to make using janbanery-shell even more painless, the key will be stored under `~/.janbanery`. **So if you want to stop using auto sign-in, just remove this file.**

Using it
--------
In order to use **Janbanery-Shell** at "full speed", you will need to learn a little of the Janbanery fluent API.
It's really simple, there are some examples bellow, but it's best to just look at the **<a href="https://github.com/ktoso/janbanery/wiki">wiki of Janbanery</a>**.

Or even better... **Use TAB completition**! It's always up to date as it's being built with reflection from Janbanery sources. Also note that if a method takes parameters,
the completition will complete `janbanery.method(` instead of `janbanery.method()` (which it would do if the method takes no arguments).

Some examples
-------------
Just to make you get the feel of Janbanery let's look at some examples right now:

**Ok, what projects can I access?**

``java
    List<Project> projects = janbanery.projects()
                                       .all();

    List<Project> projects = janbanery.usingWorkspace("janbanery").projects()
                                       .all();

    Project project = janbanery.projects().byName("janbaneryProject");

    List<Project> projects = janbanery.projects()
                                       .allAcrossWorkspaces();
``

**Get my User**

``java
    User me = janbanery.users()
                       .current();
``

**Take a look at the possible Task Types**

``java
    List<TaskType> all = janbanery.taskTypes()
                                  .all();

    TaskType any = janbanery.taskTypes()
                            .any();
``

**You can also work on columns**

```java
    Column column = new Column.Builder("Testing").capacity(5).build();

    Column last = janbanery.columns().last();
    Column beforeLast = janbanery.columns().before(last);

    column = janbanery.columns().create(column).after(beforeLast);

    janbanery.columns().create(column).beforeLast();
    janbanery.columns().move(column).toPosition(5);
```

**And here's how you could use the flows if you wanted to**

```java
    Task task = janbanery.tasks().create(task)
                         .assign().to(me) // task flow
                         .move().toNextColumn() // task movement flow
                         .move().toNextColumn() // task movement flow
                         .mark().asReadyToPull() // task flow
                         .get(); // task
```

**And there's much more...!**

* Tasks
* TaskSubscriptions
* TaskTypes
* SubTasks
* Estimates
* Comments
* Issues
* Columns
* Workspaces
* Projects
* ProjectMemberships
* Permissions

All ready to be used, so just *start coding your Kanbanery integration right now*!


Links and resources
===================

* **<a href="https://github.com/ktoso/janbanery">Janbanery</a>** - My fluent Java library which wraps the Kanbanery API
* <a href="https://kanbanery.com">Kanbanery.com</a>
* Full <a href="http://support.kanbanery.com/entries/506142-api-version-1-2">Kanbanery REST API description (v1.2)</a>
* <a href="http://www.blog.project13.pl">blog.project13.pl</a> - my blog, feel free to comment about this library there

License
=======
<img style="float:right; padding:3px; " src="https://github.com/ktoso/janbanery/raw/master/feather-small.gif" alt="Apache License 2.0"/><br/>
I'm releasing this app under the **Apache License 2.0**.

You're free to use it as you wish, the license text is attached in the LICENSE file.
You may contact me if you want this to be released on a different license, just send me an email <a href="mailto:konrad.malawski@java.pl">konrad.malawski@java.pl</a> :-)`
