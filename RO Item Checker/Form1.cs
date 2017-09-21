using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.IO;
using System.Windows.Forms;

namespace RO_Item_Checker
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {

        }

        private void openToolStripMenuItem_Click(object sender, EventArgs e)
        {
          /// Creates a file chooser window to select our text file
            openFileDialog1.Filter = "Text Document (*.txt)|*.txt|All files (*.*)|*.*";
            openFileDialog1.Title = "Open";

            openFileDialog1.InitialDirectory = @"C:\Users";
            openFileDialog1.RestoreDirectory = false;

            if (openFileDialog1.ShowDialog() == DialogResult.OK)
            {
                string file = openFileDialog1.FileName;
                string text = File.ReadAllText(file);
                /// Check the loaded text file
                textBox1.Text = checkUnequipScript(text);
            }
        }

        private string checkUnequipScript(string t)
        {
            StringReader s_in= new StringReader(t);
            String curr_line = "";
            String results = "";
            int num_results = 0;

            curr_line = s_in.ReadLine();
            while (curr_line != null)
            {
                /// Filter out commented lines, new lines, blank lines
                if (curr_line.StartsWith("//") || curr_line.StartsWith("/n") || curr_line.Equals("")) results += "";
                /// Filter out lines with no unequip scripts
                else if (curr_line.EndsWith("{}") || curr_line.EndsWith("{ }")) results += "";
                /// If we get here we have an item with an unequip script
                else
                {
                    results += curr_line + "\r\n\r\n";
                    num_results++;
                }
                curr_line = s_in.ReadLine();
            }
            /// Print out number of results and which lines had unequip scripts
            results = "Found " + num_results.ToString() + " results..." + "\r\n\r\n" + results;
            return results;
        }

        private void exitToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Application.Exit();
        }

        private void aboutToolStripMenuItem_Click(object sender, EventArgs e)
        {

        }

        private void aboutToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            AboutBox1 box = new AboutBox1();
            box.ShowDialog();
        }
    }
}
