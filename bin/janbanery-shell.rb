#!/usr/bin/env ruby

require 'lib/janbanery-core-1.4-SNAPSHOT.jar'
require 'lib/async-http-client-1.6.3.jar'
require 'lib/async-http-client-1.6.3.jar'
require 'lib/guava-r09.jar'
require 'lib/mockito-all-1.8.1.jar'
require 'lib/xpp3_min-1.1.3.4.O.jar'
require 'lib/fest-assert-1.4.jar'
require 'lib/netty-3.2.4.Final.jar'
require 'lib/xstream-1.2.2.jar'
require 'lib/fest-util-1.1.6.jar'
require 'lib/jline-0.9.9.jar'
require 'lib/slf4j-api-1.6.1.jar'
require 'lib/groovy-all-1.9.0-beta-2.jar'
require 'lib/joda-time-1.6.2.jar'
require 'lib/slf4j-nop-1.6.1.jar'
require 'lib/gson-1.7.1.jar'
require 'lib/junit-4.8.2.jar'
require 'lib/xpp3-1.1.3.4.O.jar'

require 'rubygems'
require 'terminal-table/import'

require 'java'
java_import 'pl.project13.janbanery.core.Janbanery'
java_import 'pl.project13.janbanery.core.JanbaneryFactory'

@apikey = '4f76f3cbc5ff0b6df0f22ca3803f559a8e1d7a23'

@janbanery = JanbaneryFactory.new().connect_using('konrad.malawski@llp.pl', 'llp128maximum').to_workspace('llp')
@janbanery.using_project('RTDashboard')

def c(name)
  {:value => name, :alignment => :center}
end

def col(col_name)
  @janbanery.columns().by_name(col_name)[0]
end

def task(task)
  task.title[0,20]
end



projects = @janbanery.projects().all().get(0)

@columns = @janbanery.columns().all().map { |c| c.name }

@board = table @columns

@tasks_0 = @janbanery.tasks().all_in(col(@columns[0]));
@tasks_1 = @janbanery.tasks().all_in(col(@columns[1]));
@tasks_2 = @janbanery.tasks().all_in(col(@columns[2]));
@tasks_3 = @janbanery.tasks().all_in(col(@columns[3]));
@tasks_4 = @janbanery.tasks().all_in(col(@columns[4]));
for i in 0..1 do
  row = [task(@tasks_0[i]), task(@tasks_1[i]), task(@tasks_2[i]), task(@tasks_3[i]), task(@tasks_4[i])]
   
  @board << row
end


puts @board
