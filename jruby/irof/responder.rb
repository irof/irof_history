# -*- coding: utf-8 -*-
class Responder
  def initialize(name, dictionary)
    @name = name
    @dictionary = dictionary
  end

  def response(input)
    "default"
  end

  def select_random(dictionary)
    dictionary[rand(dictionary.size)]
  end
  
  attr_reader :name
end

class WhatResponder < Responder
  def response(input)
    "#{input}ってなに？"
  end
end

class RandomResponder < Responder
  def response(input)
    select_random @dictionary.random
  end
end

class PatternResponder < Responder
  def response(input)
    @dictionary.pattern.each do |ptn_item|
      if m = input.match(ptn_item['pattern'])
        resp = select_random(ptn_item['phrases'].split '|')
        return resp.gsub /%match%/, m.to_s
      end
    end

    select_random @dictionary.random
  end
end

