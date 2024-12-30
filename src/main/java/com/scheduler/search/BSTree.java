/*
*    Team Smoliv
*    Cole Briggs, Chris Axten, Erioluwa Soyemi, Grace Kelly Osena
*    CPSC 433 F24
*/

package com.scheduler.search;
import com.scheduler.model.Schedule;

public class BSTree {

  public Schedule value;
  public BSTree left;
  public BSTree right;
  public int size, fitSum;
  
  public BSTree() {
    this.value = null;
    this.left = null;
    this.right = null;
    this.size = 0;
    this.fitSum = 0;
  }
  
  public BSTree(Schedule inpValue) {
	this.value = inpValue;
	this.left = null;
	this.right = null;
	this.size = 1;
	this.fitSum = inpValue.eval();
  }

  // Adds input to the tree
  public void add(Schedule input) {

    if(size > 0) {

      if(input.eval() >= value.eval()) {

        if(left != null) {
          left.add(input);
        }

        else {
          left = new BSTree(input);
        }
      }

      else {

        if(right != null) {
          right.add(input);
        }

	      else {
          right = new BSTree(input);
        }
      }

    }

    else {
      value = input;
    }
  
    size++;
    fitSum = fitSum + input.eval();
  }

  // Deletes the tree (reformats to null tree)
  public void kill() {

    if(size > 0) {
      if (left != null) {
        fitSum = fitSum - left.fitSum;
        left.kill();
	      left = null;
      }

      if (right != null) {
        fitSum = fitSum - right.fitSum;
        right.kill();
	      right = null;
      }

      fitSum = 0;

      value = null;

      size = 0;
      }
  }

    // Removes AT MOST "amount" number of worst schedules
    public void remove(int amount) {

        int lefttoremove = amount;
        
        int leftoriginalsize;

        if(size <= amount) {
            this.kill();
        }

        else {
          if (left != null) {
            
            leftoriginalsize = left.size;
            
            lefttoremove -= leftoriginalsize;
            
            fitSum = fitSum - left.fitSum;
            
            left.remove(amount);
            
            size -= (leftoriginalsize - left.size);
            
            if(left.size < 1) {
              left = null;
            }
          }

          if (lefttoremove > 0) {

            BSTree temp = new BSTree();
            temp = this.right;
            this.right = null;
            this.right = temp.right;
            this.left = temp.left;
            size = temp.size;
            value = temp.value;
            fitSum = temp.fitSum;
            temp = null;
            lefttoremove = lefttoremove - 1;
            if(lefttoremove > 0) {
              this.remove(lefttoremove);
            }

          }
        }
    }


  public Schedule getWorst() {
    if(size > 0) {
      if(left == null) {
        return value;
      } else {
        return left.getWorst();
      }
    }
    else return null;
  }


  // Returns best schedule (for mutation)
  // returns NULL if no schedules exist
  public Schedule getBest() {
    if(size > 0) {
      if(right == null) {
        return value;
      } else {
        return right.getBest();
      }
    }
    else return null;
  }

  // Returns 2nd best schedule (for crossover)
  // Returns NULL if less than 2 schedules exist
  public Schedule get2Best() {
    if(size > 1) {
      if(right != null)
        if(right.size > 1)
	  return right.get2Best();
	else
	  return value;
      else
        return left.getBest();
    }
    else return null;
  }
}
