package idv.const_x.math;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description <pre>
 *  按指定权重分摊数值
 * </pre>
 * @Author const.x
 * @Date 2023-07-13
 */
public class AllocationUtils {


    public static <T> List<Result<T>> allocation(List<TargetWeight<T>> targetWeights, int scale, BigDecimal ... toAllocations) {
        //如果只有一个,直接赋值
        if (targetWeights.size() == 1) {
            TargetWeight<T> param = targetWeights.get(0);
            Result<T> result = new Result<T>();
            result.setTarget(param.target);
            result.setResult(toAllocations);
            result.setWeight(param.weight);
            result.setSum(param.weight);
            result.setDesc("单个继承");
            return Arrays.asList(result);
        }

        BigDecimal[] zeros = new BigDecimal[toAllocations.length];
        for (int i = 0; i < toAllocations.length; i++) {
            zeros[i] = BigDecimal.ZERO;
        }

        List<TargetWeight<T>> needCalculate = new ArrayList<>();
        List<Result<T>> results = new ArrayList<>();

        BigDecimal sum = BigDecimal.ZERO;
        boolean isAllZero = true;
        for (TargetWeight<T> ratio : targetWeights) {
            if (ratio.weight != null && ratio.weight.compareTo(BigDecimal.ZERO) != 0) {
                sum = sum.add(ratio.weight);
                needCalculate.add(ratio);
                isAllZero = false;
            }else{
                //分摊权重为0的 不参与分摊计算 相应金额直接设置为0 防止尾差记录到权重为0的对象上;
                Result<T> result = new Result<T>();
                result.setTarget(ratio.target);
                result.setResult(zeros);
                result.setWeight(ratio.weight);
                result.setSum(sum);
                result.setDesc("0.00/"+sum);
                results.add(result);
            }
        }

        if (isAllZero) {
            throw new IllegalArgumentException("分摊权重全部为0 无法分摊");
        }

        if (sum.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("分摊除数0 无法分摊");
        }

        int counter = 0;

        BigDecimal[] alreadyShareds = new BigDecimal[toAllocations.length];
        for (TargetWeight<T> param : needCalculate) {
            BigDecimal[] shared = new BigDecimal[toAllocations.length];

            Result<T> result = new Result<T>();
            result.setTarget(param.target);
            result.setResult(shared);
            result.setWeight(param.weight);
            result.setSum(sum);


            //最后一笔算尾差
            if (counter == needCalculate.size() - 1) {
                for (int i = 0; i < toAllocations.length; i++) {
                    BigDecimal alreadyShared = alreadyShareds[i];
                    if (alreadyShared == null) {
                        alreadyShared = BigDecimal.ZERO;
                    }
                    shared[i] = toAllocations[i].subtract(alreadyShared);
                }
                result.setDesc("尾差值");
            }else{
                for (int i = 0; i < toAllocations.length; i++) {
                    BigDecimal toShare = toAllocations[i];
                    if (toShare.compareTo(BigDecimal.ZERO) == 0) {
                        shared[i] = BigDecimal.ZERO;
                        result.setDesc(param.weight+"/"+sum);
                    }else{
                        BigDecimal decimal = toShare.multiply(param.weight).divide(sum,scale,BigDecimal.ROUND_HALF_UP);
                        if (alreadyShareds[i] == null) {
                            alreadyShareds[i] = BigDecimal.ZERO;
                        }
                        //如果总的已分摊金额已经大于分摊金额(因为进位),则后续的金额都设置为0
                        if ((toShare.compareTo(BigDecimal.ZERO) > 0 && toShare.subtract(alreadyShareds[i]).compareTo(decimal) < 0)
                                || (toShare.compareTo(BigDecimal.ZERO) < 0 && toShare.subtract(alreadyShareds[i]).compareTo(decimal) > 0)
                        ) {
                            shared[i] = toShare.subtract(alreadyShareds[i]);
                            alreadyShareds[i] = toShare;
                            result.setDesc("剩余不足");
                        }else {
                            alreadyShareds[i] = alreadyShareds[i].add(decimal);
                            shared[i] = decimal;
                            result.setDesc(param.weight+"/"+sum);
                        }
                    }

                }
            }
            counter++;

            results.add(result);
        }
        return  results;


    }


    /**
     * 分摊目标及对应权重
     * @param <T>
     */
    public static class TargetWeight<T>{

        private T target;

        private BigDecimal weight;

        public TargetWeight(T target, BigDecimal weight) {
            this.target = target;
            this.weight = weight;
        }

        public T getTarget() {
            return target;
        }

        public void setTarget(T target) {
            this.target = target;
        }

        public BigDecimal getWeight() {
            return weight;
        }

        public void setWeight(BigDecimal weight) {
            this.weight = weight;
        }
    }

    public static class Result<T>{

        private T target;

        private BigDecimal weight;

        private BigDecimal sum;

        private BigDecimal[] result;

        private String desc;


        public T getTarget() {
            return target;
        }

        public void setTarget(T target) {
            this.target = target;
        }


        public BigDecimal getWeight() {
            return weight;
        }

        public void setWeight(BigDecimal weight) {
            this.weight = weight;
        }

        public BigDecimal getSum() {
            return sum;
        }

        public void setSum(BigDecimal sum) {
            this.sum = sum;
        }

        public BigDecimal[] getResult() {
            return result;
        }

        public void setResult(BigDecimal[] result) {
            this.result = result;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    public static void main(String[] args) {
        List<TargetWeight<String>> radios = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            TargetWeight<String> weight = new TargetWeight("A"+i,BigDecimal.ZERO);
            radios.add(weight);
        }
        TargetWeight<String> weight = new TargetWeight("B1",BigDecimal.ONE);
        radios.add(weight);
        List<Result<String>> results = allocation(radios, 2, BigDecimal.valueOf(100),BigDecimal.valueOf(200));

        BigDecimal total = BigDecimal.ZERO;
        for (Result<String> result : results) {
            System.out.println(result.target + " " + result.desc + " "+ result.result[0]);
            total = total.add(result.result[0]);
        }
        System.out.println(total);
    }
}
