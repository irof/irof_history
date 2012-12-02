# -*- coding: utf-8 -*-
include Java

require 'rubygems'

require 'rubeus'
require 'irof'
require 'responder'

include Rubeus::Swing

JFrame.new 'irof' do |frame|
  frame.layout = java.awt.FlowLayout.new

  @labelof = JLabel.new(ImageIcon.new 'image/irof.gif')
  @input = JTextField.new 20

  JButton.new '話しかける' do
    irof = Irof.new('irof')
    @response.text = irof.dialoque @input.text
  end

  @response = JTextField.new 20

  frame.pack
  frame.show
end

