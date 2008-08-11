package matt.emd;

/*
 * Signatures.java
 *
 * Created on 27. April 2004, 13:37
 */

import java.util.*;
import javax.vecmath.*;

/**
 * This is an interface for the ground distance used, iff the costmatrix is calculated by specification of the features of both sigantures.<br>
 * It could implement the euclidian distance for example.
 */
interface GroundDistance {
    /**
     * Returns the distance beween a specified P-feature and a speficied Q-feature
     * @param pFeature The P-Feature
     * @param qFeature The Q-Feature
     * @return The distance between the both features
     */    
    public float calculate(Object pFeature, Object qFeature);
}
 
/**
 * This class represents to signatures for which a distance should be calculated.
 * @author Till Schulte-Coerne
 */
public class Signatures {
    /** The internal cost-matrix without the dummys */    
    private float[][] mCostMatrix; //size: m x n 

    /** The internal vector in which the costs for a possibly needed P-dummyfeature are stored */    
    private float[] mPDummyCostVector; //size: n
    
    /** The internal vector in which the costs for a possibly needed Q-dummyfeature are stored */    
    private float[] mQDummyCostVector; //size: m
    
    /** The internal weights from signature P without dummy cost */        
    private int[] mPWeights; //size: m
    
    /** The internal weights from signature Q without dummy cost */    
    private int[] mQWeights; //size: n
    
    /** The linear sum of P-costs without dummy cost */    
    private int mPWeightSum = 0;
    
    /** The linear sum of Q-costs without dummy cost */    
    private int mQWeightSum = 0;
    
    /** The dummy costs of P if needed [PDummyWeight + QDummyWeight = Max(PDummyWeight, QDummyWeight)] */    
    private int mPDummyWeight = 0;
    
    /** The dummy costs of Q if needed [PDummyWeight + QDummyWeight = Max(PDummyWeight, QDummyWeight)] */    
    private int mQDummyWeight = 0;
    
    /**
     * Calculates if and which dummy-weight is needed
     */    
    private void calculateDummyWeights() {
        int diff = mPWeightSum - mQWeightSum;
        if (diff >= 0) { //Sum(P) >= Sum(Q)
            mPDummyWeight = 0;
            mQDummyWeight = diff;
        }
        else { //Sum(P) < Sum(Q)
            mPDummyWeight = -diff;
            mQDummyWeight = 0;
        }
     }
    
    /**
     * Creates a new instance of Signatures
     * @param costMatrix The Cost-Matrix for transporting weights from P to Q (starting at [0, 0])
     * @throws InvalidMatrixException If the specified Matrix is invalid
     * @throws InvalidDummyCostException If the dimensions of the dummyvectors are not matching the dimensions of the costmatrix
     */
    public Signatures(float[][] costMatrix) throws InvalidMatrixException, InvalidDummyCostException {
        this(costMatrix, (float[])null, (float[])null);
    }
    
    /**
     * Creates a new instance of Signatures
     * @param costMatrix The Cost-Matrix for transporting weights from P to Q (starting at [0, 0])
     * @param pDummyCostVector The Cost-Vector for Siganture P if required
     * @param qDummyCostVector The Cost-Vector for Siganture Q if required
     * @throws InvalidMatrixException If the specified Matrix is invalid
     * @throws InvalidDummyCostException If the dimensions of the dummyvectors are not matching the dimensions of the costmatrix
     */
    public Signatures(float[][] costMatrix, float[] pDummyCostVector, float[] qDummyCostVector) throws InvalidMatrixException, InvalidDummyCostException {
        if (costMatrix == null || costMatrix.length < 1 || costMatrix[0].length < 1) //Don't accept empty matrices
            throw new InvalidMatrixException();
        for (int i = 1; i < costMatrix.length; i++) //Verify the correctness of the Matrix
            if (costMatrix[i].length != costMatrix[0].length)
                throw new InvalidMatrixException();
        mCostMatrix = (float[][]) costMatrix.clone(); //Copy the matrix
        mPWeights = new int[mCostMatrix.length]; //Init the P-Weights
        mQWeights = new int[mCostMatrix[0].length]; //Init the Q-Weights
        if (pDummyCostVector == null) //p-Dummys cost nothing
            mPDummyCostVector = new float[mCostMatrix[0].length];
        else { //p-Dummy-costs given
            if (pDummyCostVector.length != mCostMatrix[0].length)
                throw new InvalidDummyCostException();
            mPDummyCostVector = (float[]) pDummyCostVector.clone();
        }
        if (qDummyCostVector == null) //q-Dummys cost nothing
            mQDummyCostVector = new float[mCostMatrix.length];
        else { //q-Dummy-costs given
            if (qDummyCostVector.length != mCostMatrix.length)
                throw new InvalidDummyCostException();
            mQDummyCostVector = (float[]) qDummyCostVector.clone();
        }
    }
    
    /**
     * Creates a new instance of Signatures
     * @param pFeatures The features of signature P
     * @param qFeatures The features of signature Q
     * @param groundDistance The function for calculating a distance between to features of P and Q
     * @throws InvalidFeatureException If no features are supplyed
     */
    public Signatures(Object[] pFeatures, Object[] qFeatures, GroundDistance groundDistance) throws InvalidFeatureException {
        this(pFeatures, qFeatures, groundDistance, null, null);
    }

    /**
     * Creates a new instance of Signatures
     * @param pFeatures The features of signature P
     * @param qFeatures The features of signature Q
     * @param groundDistance The function for calculating a distance between to features of P and Q
     * @param pDummyFeature The Dummy-Feature of P if requred (null means zero-cost dummy-features for P)
     * @param qDummyFeature The Dummy-Feature of Q if requred (null means zero-cost dummy-features for Q)
     * @throws InvalidFeatureException If no features are supplyed
     */
    public Signatures(Object[] pFeatures, Object[] qFeatures, GroundDistance groundDistance, Object pDummyFeature, Object qDummyFeature) throws InvalidFeatureException {
        if (pFeatures == null || pFeatures.length < 1 || qFeatures == null || qFeatures.length < 1)
            throw new InvalidFeatureException();
        mCostMatrix = new float[pFeatures.length][qFeatures.length];
        int i, j;
        for (i = 0; i < pFeatures.length; i++) {
            for (j = 0; j < qFeatures.length; j++) {
                mCostMatrix[i][j] = groundDistance.calculate(pFeatures[i], qFeatures[j]);
            }
        }
        mPWeights = new int[mCostMatrix.length]; //Init the P-Weights
        mQWeights = new int[mCostMatrix[0].length]; //Init the Q-Weights
        mPDummyCostVector = new float[mCostMatrix[0].length];
        if (pDummyFeature != null) {
            for (j = 0; j < qFeatures.length; j++)
                mPDummyCostVector[j] = groundDistance.calculate(pDummyFeature, qFeatures[j]);
        }
        mQDummyCostVector = new float[mCostMatrix.length];
        if (qDummyFeature != null) {
            for (i = 0; i < pFeatures.length; i++)
                mQDummyCostVector[i] = groundDistance.calculate(qDummyFeature, pFeatures[i]);
        }
    }

    /**
     * Returns the feature-count of signature P (including Dummys if needed)
     * @return The feature-count
     */    
    public int getPSize() {
        return mCostMatrix.length + (mPDummyWeight > 0 ? 1 : 0);
    }

    /**
     * Returns the feature-count of signature Q (including Dummys if needed)
     * @return The feature-count
     */    
    public int getQSize() {
        return mCostMatrix[0].length + (mQDummyWeight > 0 ? 1 : 0);
    }
    
    /**
     * Returns the linear sum of the weights of signature P (ex!cluding Dummyweight)
     * @return The feature-count
     */    
    public int getPWeightSum() {
        return mPWeightSum;
    }

    /**
     * Returns the linear sum of the weights of signature Q (ex!cluding Dummyweight)
     * @return The feature-count
     */    
    public int getQWeightSum() {
        return mQWeightSum;
    }

    /**
     * Gets the weight for the specified feature in P (including Dummys if needed)
     * @param index The index of the feature (Starting at 1)
     * @return The weight
     * @throws IndexOutOfBoundsException If index is not matching the signature
     */    
    public int getPWeight(int index) throws IndexOutOfBoundsException  {
        if (index == mPWeights.length + 1 && mPDummyWeight > 0)
            return mPDummyWeight;
        if (index < 1 || index > mPWeights.length)
            throw new IndexOutOfBoundsException();
        return mPWeights[index - 1];
    }
    
    /**
     * Sets the weight for the specified feature in P
     * @param index The index of the feature (Starting at 1)
     * @param weight The weight
     * @throws IndexOutOfBoundsException If index is not matching the signature
     */    
    public void setPWeight(int index, int weight) throws IndexOutOfBoundsException  {
        if (index < 1 || index > mPWeights.length)
            throw new IndexOutOfBoundsException();
        mPWeightSum += weight - mPWeights[index - 1];
        mPWeights[index - 1] = weight;
        calculateDummyWeights();
    }
    
    /**
     * Sets the weights of all features in P
     * @param weights The weights vector
     * @throws InvalidWeightsVectorException If index is not matching the signature
     */    
    public void setPWeights(int[] weights) throws InvalidWeightsVectorException  {
        if (weights == null || weights.length != mPWeights.length)
            throw new InvalidWeightsVectorException();
        mPWeightSum = 0;
        mPWeights = (int[])weights.clone();
        for (int i = 0; i < mPWeights.length; i++)
            mPWeightSum += mPWeights[i];
        calculateDummyWeights();
    }
    
    /**
     * Gets the weight for the specified feature in Q (including Dummys if needed)
     * @param index The index of the feature (Starting at 1)
     * @return The weight
     * @throws IndexOutOfBoundsException If index is not matching the signature
     */    
    public int getQWeight(int index) throws IndexOutOfBoundsException  {
        if (index == mQWeights.length + 1 && mQDummyWeight > 0)
            return mQDummyWeight;
        if (index < 1 || index > mQWeights.length)
            throw new IndexOutOfBoundsException();
        return mQWeights[index - 1];
    }
    
    /**
     * Sets the weight for the specified feature in Q
     * @param index The index of the feature (Starting at 1)
     * @param weight The weight
     * @throws IndexOutOfBoundsException If index is not matching the signature
     */    
    public void setQWeight(int index, int weight) throws IndexOutOfBoundsException  {
        if (index < 1 || index > mQWeights.length)
            throw new IndexOutOfBoundsException();
        mQWeightSum += weight - mQWeights[index - 1];
        mQWeights[index - 1] = weight;
        calculateDummyWeights();
    }
    
    /**
     * Sets the weights of all features in Q
     * @param weights The weights vector
     * @throws InvalidWeightsVectorException If index is not matching the signature
     */    
    public void setQWeights(int[] weights) throws InvalidWeightsVectorException  {
        if (weights == null || weights.length != mQWeights.length)
            throw new InvalidWeightsVectorException();
        mQWeightSum = 0;
        mQWeights = (int[])weights.clone();
        for (int i = 0; i < mQWeights.length; i++)
            mQWeightSum += mQWeights[i];
        calculateDummyWeights();
    }
    
    /**
     * Gets the cost-entry for the specified index (including Dummys if needed)
     * @param i (Starting at 1)
     * @param j (Starting at 1)
     * @return The cost c<sub>ij</sub>
     * @throws IndexOutOfBoundsException If index is not matching the signature
     */    
    public float getCost(int i, int j) throws IndexOutOfBoundsException  {
        if (i < 1 || j < 1)
            throw new IndexOutOfBoundsException();
        if (i == mCostMatrix.length + 1 && mPDummyWeight > 0 && j <= mCostMatrix[0].length)
            return mPDummyCostVector[j - 1];
        else if (j == mCostMatrix[0].length + 1 && mQDummyWeight > 0 && i <= mCostMatrix.length)
            return mQDummyCostVector[i - 1];
        else if (i <= mCostMatrix.length && j <= mCostMatrix[0].length)
            return mCostMatrix[i - 1][j - 1];
        else
            throw new IndexOutOfBoundsException();
    }
}
