package com.company;

import java.io.*;
import java.util.*;

public class Open_Addressing {
	public int m; // number of SLOTS
	public int A; // the default random number
	int w;
	int r;
	int seed;
	public int[] Table;
	public static final double MAX_LOAD_FACTOR = 0.75;
	int size; // number of elements stored in the hash table

	protected Open_Addressing(int w, int seed, int A) {
		this.seed = seed;
		this.w = w;
		this.r = (int) (w - 1) / 2 + 1;
		this.m = power2(r);
		if (A == -1) {
			this.A = generateRandom((int) power2(w - 1), (int) power2(w), seed);
		} else {
			this.A = A;
		}
		this.Table = new int[m];
		for (int i = 0; i < m; i++) {
			Table[i] = -1;
		}
		this.size = 0;
	}

	/**
	 * Calculate 2^w
	 */
	public static int power2(int w) {
		return (int) Math.pow(2, w);
	}

	public static int generateRandom(int min, int max, int seed) {
		Random generator = new Random();
		if (seed >= 0) {
			generator.setSeed(seed);
		}
		int i = generator.nextInt(max - min - 1);
		return i + min + 1;
	}

	/**
	 * Implements the hash function g(k)
	 */
	public int probe(int key, int i) {
		Chaining h = new  Chaining(w,seed,A);
		return ( (h.chain(key) + i ) % power2(r));
	}

	/**
	 * Inserts key k into hash table. Returns the number of collisions encountered
	 */
	public int insertKey(int key) {

		int collisions=0;
		if (size!=m){
			for (int i=0;i<m;i++) {
				if (Table[probe(key, i)] != -1) { // if it isnt empty, then collision
					collisions++;
				}else {
					Table[probe(key,i)]=key; // if it is empty, no collision
					size++;
					break;
				}
			}
		}
		return collisions;
	}
	/**
	 * Sequentially inserts a list of keys into the HashTable. Outputs total number of collisions
	 */
	public int insertKeyArray(int[] keyArray) {
		int collision = 0;
		for (int key : keyArray) {
			collision += insertKey(key);
		}
		return collision;
	}

	/**
	 //* @param the key k to be searched
	 * @return an int array containing 2 elements:
	 * first element = index of k in this.Table if the key is present, = -1 if not present
	 * second element = number of collisions occured during the search
	 */
	public int[] searchKey(int k) {

		int[] output = {-1, -1};
		int collisions=0;

		for (int i=0;i<m;i++) {
			if (Table[probe(k,i)]==k) { // if the hash function for given key with random index
				output[0]=probe(k,i);
				break;
			} else {
				collisions++;
			}
		}
		output[1]=collisions;
		return output;
	}
	/**
	 * Removes key k from hash table. Returns the number of collisions encountered
	 */
	public int removeKey(int k){

		/*int collisions=0;
		for (int i=0;i<m;i++) {
			if (Table[probe(k,i)]==k) { // if the hash function for given key with random index is equal to the key
				Table[probe(k,i)]=-1; // set it back to empty
				break;
			} else { collisions++; // if we did not find it, collision }
		}
		return collisions; */

		int[] info=searchKey(k); // 0: index of the key 1: collisions
		if (info[0]!=-1){
			Table[info[0]]=-1;
			size--;
		}
		return info[1];
	}

	/**
	 * Inserts key k into hash table. Returns the number of collisions encountered,
	 * and resizes the hash table if needed
	 */
	public int insertKeyResize(int key) {

		float load_factor= (float) (size+1)/m; // we want to see if the laod factor will be bigger on the next

		if (load_factor>MAX_LOAD_FACTOR) {
			int[] Table_copy=new int[m];
			for (int i=0;i<m;i++) {Table_copy[i]=this.Table[i];} // create a needed copy

			w=w+2;
			size=0;
			A=generateRandom((int) power2(w - 1), (int) power2(w), seed);
			r = (int) (w - 1) / 2 + 1;
			m = power2(r);

			Table=new int[m];
			for (int i=0; i<m;i++) {Table[i]=-1;} // Fill the list with empty values
			for (int i=0;i<Table_copy.length;i++) {
				if (Table_copy[i]==-1) {} else {insertKey(Table_copy[i]);} // if empty, go next, if not, insert the key
			}
		}
		return insertKey(key);
	}


	/**
	 * Sequentially inserts a list of keys into the HashTable, and resize the hash table
	 * if needed. Outputs total number of collisions
	 */
	public int insertKeyArrayResize(int[] keyArray) {
		int collision = 0;
		for (int key : keyArray) {
			collision += insertKeyResize(key);
		}
		return collision;
	}

	/**
	 //* @param the key k to be searched (and relocated if needed)
	 * @return an int array containing 2 elements:
	 * first element = index of k in this.Table (after the relocation) if the key is present, 
	 * 				 = -1 if not present
	 * second element = number of collisions occured during the search
	 */
	public int[] searchKeyOptimized(int k) {

		int[] output = searchKey(k);
		if(output[0]!=-1) { // if the key i not present
			for (int i = 0; i < size; i++) {
				if (Table[probe(k, i)] == -1) {
					Table[probe(k, i)] = k; // fill the old slot with the key given it should have been filled by it
					Table[output[0]] = -1; // remove that key form old position
					break;
				}
				if (Table[probe(k,i)]==k) {break;} // in case we find the key , we dont want to switch it back further
			}
		}
		return output;
	}
	/**
	 * @return an int array of n keys that would collide with key k
	 */
	public int[] collidingKeys(int k, int n, int w) {

		int[] output = new int[n];
		//Chaining h = new  Chaining(w,seed,A);
		//int dividend=Math.floorDiv(this.A*k,power2(w));
		//int remainder=h.chain(k);

		for (int i=0;i<n;i++) {output[i]=k+i*power2(w);}

		return output;
	}
}
