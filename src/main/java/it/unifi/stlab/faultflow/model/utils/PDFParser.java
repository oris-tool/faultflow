/*
 * This program is part of the ORIS Tool.
 * Copyright (C) 2011-2023 The ORIS Authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package it.unifi.stlab.faultflow.model.utils;

import org.apache.commons.math3.distribution.*;
import org.oristool.math.OmegaBigDecimal;
import org.oristool.math.expression.Expolynomial;
import org.oristool.math.function.EXP;
import org.oristool.math.function.Erlang;
import org.oristool.math.function.GEN;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;

import java.math.BigDecimal;


public class PDFParser {
    /*We assume
        ""-> dirac(0)
        uniform(lower, upper) s.t. lower<upper
        dirac(value)
        exp(lambda)
        erlang(shape, lambda)
        gaussian(mean, variance)
     */

    public static StochasticTransitionFeature parseStringToStochasticTransitionFeature(String arisingPDF) {
        String typePDF = arisingPDF.toLowerCase().replaceAll("\\s*\\([^()]*\\)\\s*", "");
        String arguments = arisingPDF.substring(typePDF.length() + 1, arisingPDF.length() - 1);
        String[] args;
        switch (typePDF) {
            case "": //assume dirac(0)
                return StochasticTransitionFeature.newDeterministicInstance("0");
            case "uniform":
                //uniform(eft, lft)
                args = arguments.split(",");
                return StochasticTransitionFeature.newUniformInstance(args[0], args[1]);
            case "dirac":
                //dirac(value)
                return StochasticTransitionFeature.newDeterministicInstance(arguments);
            case "exp":
                //exp(rate) rate=1/mean
                double rate = checkDivision(arguments);
                return StochasticTransitionFeature.newExponentialInstance(String.valueOf(rate));
            case "erlang":
                //erlang(shape, λ); rate=λ
                args = arguments.split(",");
                double lambda = checkDivision(args[1]);
                return StochasticTransitionFeature.newErlangInstance(Integer.parseInt(args[0]), String.valueOf(lambda));
            case "gaussian":
                //gaussian(mean, variance)
                //a=μ-radq(3)*(σ); b=μ+radq(3)*(σ)
                args = arguments.split(",");
                double factor = Math.sqrt(3 * Double.parseDouble(args[1]));
                String a = "" + (Double.parseDouble(args[0]) - factor);
                String b = "" + (Double.parseDouble(args[0]) + factor);
                return StochasticTransitionFeature.newUniformInstance(a, b);
            case "expoly":
                args = arguments.split(",");
                String density = args[0];
                OmegaBigDecimal eft = new OmegaBigDecimal(args[1]);
                OmegaBigDecimal lft = new OmegaBigDecimal(args[2]);
                if (Expolynomial.isValid(density))
                    return StochasticTransitionFeature.newExpolynomial(density, eft, lft);
                else
                    throw new UnsupportedOperationException("Function not well formed");
            default:
                throw new UnsupportedOperationException("PDF not supported");
        }
    }

    public static RealDistribution parseStringToRealDistribution(String arisingPDF) {
        String typePDF = arisingPDF.toLowerCase().replaceAll("\\s*\\([^()]*\\)\\s*", "");
        String arguments = arisingPDF.substring(typePDF.length() + 1, arisingPDF.length() - 1);
        String[] args;
        switch (typePDF) {
            case "": //assume dirac(0)
                return new NormalDistribution(0.0, Double.MIN_VALUE);
            case "uniform":
                //uniform(lower, upper)
                args = arguments.split(",");
                return new UniformRealDistribution(Double.parseDouble(args[0]), Double.parseDouble(args[1]));
            case "dirac":
                //dirac(value)
                return new NormalDistribution(Double.parseDouble(arguments), Double.MIN_VALUE);
            case "exp":
                //input: exp(lambda)
                //realDistribution: exp(mean)
                double rate = checkDivision(arguments);
                return new ExponentialDistribution(1 / rate);
            case "erlang":
                args = arguments.split(",");
                //assuming args[1]=shape is integer (erlang def.)
                //input: erlang(shape, lambda)
                //realDistribution: erlang(shape, scale(= 1/lambda))
                double lambda = checkDivision(args[1]);
                return new GammaDistribution((Integer.parseInt(args[0])), 1 / lambda);
            case "gaussian":
                //input: gaussian(mean, variance)
                //realDistribution: gaussian(mean, standardDev)
                args = arguments.split(",");//args[0] is the mean, args[1] is the Variance
                return new NormalDistribution(Double.parseDouble(args[0]), Math.sqrt(Double.parseDouble(args[1])));
            case "expoly":
                args = arguments.split(",");
                return new ExpolynomialDistribution(args[0], new OmegaBigDecimal(args[1].trim()), new OmegaBigDecimal(args[2].trim()));
            default:
                throw new UnsupportedOperationException("PDF not supported");
        }
    }

    public static RealDistribution parseStochasticTransitionFeatureToRealDistribution(StochasticTransitionFeature stochasticTransitionFeature) {
        if (Erlang.class.equals(stochasticTransitionFeature.density().getClass())) {
            return parseStringToRealDistribution("erlang(" + ((Erlang) stochasticTransitionFeature.density()).getShape() + "," +
                    Double.parseDouble(String.valueOf(((Erlang) stochasticTransitionFeature.density()).getLambda())) + ")");
        } else if (GEN.class.equals(stochasticTransitionFeature.density().getClass())) {
            String domain = ((GEN) stochasticTransitionFeature.density()).getDomain().toString().replaceAll(" ", "").replace("\n", "");
            String[] bounds = domain.split("<=");
            if (!((GEN) stochasticTransitionFeature.density()).getDensity().isConstant()) {
                return parseStringToRealDistribution("expoly(" + ((GEN) stochasticTransitionFeature.density()).getDensity().toString() + "," + bounds[0] + "," + bounds[2] + ")");
            }
            if (bounds[0].equals(bounds[2])) {
                return parseStringToRealDistribution("dirac(" + bounds[0] + ")");
            } else {
                return parseStringToRealDistribution("uniform(" + bounds[0] + "," + bounds[2] + ")");
            }
        } else if (EXP.class.equals(stochasticTransitionFeature.density().getClass())) {
            return parseStringToRealDistribution("exp(" + ((EXP) stochasticTransitionFeature.density()).getLambda().intValue() + ")");

        } else
            throw new UnsupportedOperationException("This type of StochasticTransitionFeature is unsupported");
    }

    public static StochasticTransitionFeature parseRealDistributionToStochasticTransitionFeature(RealDistribution realDistribution) {
        if (realDistribution.getClass().equals(UniformRealDistribution.class)) {
            return parseStringToStochasticTransitionFeature("uniform(" + realDistribution.getSupportLowerBound() + "," + realDistribution.getSupportUpperBound() + ")");
        } else if (realDistribution.getClass().equals(NormalDistribution.class)) {
            if (((NormalDistribution) realDistribution).getStandardDeviation() == Double.MIN_VALUE)
                return parseStringToStochasticTransitionFeature("dirac(" + realDistribution.getNumericalMean() + ")");
            else {
                return parseStringToStochasticTransitionFeature("gaussian(" + realDistribution.getNumericalMean() + "," + realDistribution.getNumericalVariance() + ")");
            }
        } else if (realDistribution.getClass().equals(ExponentialDistribution.class)) {
            return parseStringToStochasticTransitionFeature("exp(1/" + ((ExponentialDistribution) realDistribution).getMean() + ")");
        } else if (realDistribution.getClass().equals(GammaDistribution.class)) {
            return parseStringToStochasticTransitionFeature("erlang(" + (int) ((GammaDistribution) realDistribution).getShape() + ",1/" + ((GammaDistribution) realDistribution).getScale() + ")");
        } else if (realDistribution.getClass().equals(ExpolynomialDistribution.class)) {
            return parseStringToStochasticTransitionFeature("expoly(" + ((ExpolynomialDistribution) realDistribution).getDensity() + "," + ((ExpolynomialDistribution) realDistribution).getEft() + "," + ((ExpolynomialDistribution) realDistribution).getLft() + ")");
        } else
            throw new UnsupportedOperationException("This type of RealDistribution is unsupported");
    }

    public static String parseStochasticTransitionFeatureToString(StochasticTransitionFeature stochasticTransitionFeature) {
        if (Erlang.class.equals(stochasticTransitionFeature.density().getClass())) {
            return "erlang(" + ((Erlang) stochasticTransitionFeature.density()).getShape() + "," + Double.parseDouble(String.valueOf(((Erlang) stochasticTransitionFeature.density()).getLambda())) + ")";
        } else if (GEN.class.equals(stochasticTransitionFeature.density().getClass())) {
            if (!((GEN) stochasticTransitionFeature.density()).getDensity().isConstant())
                return "expoly(" + ((GEN) stochasticTransitionFeature.density()).getDensity().toString() + "," +
                        stochasticTransitionFeature.density().getDomainsEFT().toString() + "," +
                        stochasticTransitionFeature.density().getDomainsLFT().toString();
            String domain = ((GEN) stochasticTransitionFeature.density()).getDomain().toString().replaceAll(" ", "").replace("\n", "");
            String[] bounds = domain.split("<=");
            if (bounds[0].equals(bounds[2]))
                return "dirac(" + bounds[0] + ")";
            else
                return "uniform(" + bounds[0] + "," + bounds[2] + ")";
        } else if (EXP.class.equals(stochasticTransitionFeature.density().getClass())) {
            return "exp(" + Double.parseDouble(String.valueOf(((EXP) stochasticTransitionFeature.density()).getLambda())) + ")";
        } else
            throw new UnsupportedOperationException("This type of StochasticTransitionFeature is unsupported");
    }

    public static String parseRealDistributionToString(RealDistribution realDistribution) {
        if (realDistribution.getClass().equals(NormalDistribution.class)) {
            if (((NormalDistribution) realDistribution).getStandardDeviation() == Double.MIN_VALUE) {
                return "dirac(" + (int) ((NormalDistribution) realDistribution).getMean() + ")";
            } else {
                return "gaussian(" + (int) ((NormalDistribution) realDistribution).getMean() + "," + (int) (realDistribution.getNumericalVariance()) + ")";
            }
        } else if (realDistribution.getClass().equals(UniformRealDistribution.class))
            return "uniform(" + (int) realDistribution.getSupportLowerBound() + "," + (int) realDistribution.getSupportUpperBound() + ")";
        else if (realDistribution.getClass().equals(ExponentialDistribution.class)) {
            return "exp(" + (1 / (((ExponentialDistribution) realDistribution).getMean())) + ")";
        } else if (realDistribution.getClass().equals(GammaDistribution.class)) {
            return "erlang(" + (int) ((GammaDistribution) realDistribution).getShape() + "," + (1 / ((GammaDistribution) realDistribution).getScale()) + ")";
        } else if (realDistribution.getClass().equals(ExpolynomialDistribution.class)) {
            return "expoly(" + ((ExpolynomialDistribution) realDistribution).getDensity() + "," + ((ExpolynomialDistribution) realDistribution).getEft() + "," + ((ExpolynomialDistribution) realDistribution).getLft() + ")";
        } else
            throw new UnsupportedOperationException("This type of RealDistribution is unsupported");

    }

    public static double checkDivision(String arg) {
        if (arg.contains("/")) {
            String[] factors = arg.split("/");
            Double num = Double.parseDouble(factors[0]);
            Double denom = Double.parseDouble(factors[1]);
            return num / denom;
        } else {
            return Double.parseDouble(arg);
        }
    }
/*
    public static GEN parseStringToFunction(String expolyString){
        String typePDF = expolyString.toLowerCase().replaceAll("\\s*\\([^()]*\\)\\s*", "");
        String arguments = expolyString.substring(typePDF.length() + 1, expolyString.length() - 1);
        String[] args = arguments.split(",");
        String density = args[0];
        OmegaBigDecimal eft = new OmegaBigDecimal(args[1]);
        OmegaBigDecimal lft = new OmegaBigDecimal(args[2].trim());
        return GEN.newExpolynomial(density, eft, lft);

    }

 */

    public static BigDecimal generateSample(String pdf) {
        /*
        String typePDF = pdf.toLowerCase().replaceAll("\\s*\\([^()]*\\)\\s*", "");
        if(typePDF.equals("expoly")) {
            //if it's expoly sample like this:
            MetropolisHastings metropolisHastings = new MetropolisHastings(parseStringToFunction(pdf));
            return metropolisHastings.getSample();
        }


        else//else, parse into real distribution and sample

         */
        return BigDecimal.valueOf(PDFParser.parseStringToRealDistribution(pdf).sample());

    }
}
