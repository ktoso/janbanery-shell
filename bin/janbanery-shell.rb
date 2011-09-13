#!/usr/bin/env ruby

require 'rubygems'
require 'terminal-table/import'

b = table do |t|
  t.headings = "Proposed", "In Progress", "Testing"
  t << ["Deploy WebService", "Nothing", "What should I test?"]
  t << ["Do good code", "Sleeping", ""]
end

puts b
