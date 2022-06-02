package interpreter.expr;

import org.w3c.dom.Text;
import java.lang.Math;

import interpreter.util.Utils;
import interpreter.value.NumberValue;
import interpreter.value.TextValue;
import interpreter.value.Value;
import interpreter.value.BooleanValue;;

public class BinaryExpr extends Expr {

    public enum Op {
        AndOp,
        OrOp,
        EqualOp,
        NotEqualOp,
        LowerThanOp,
        LowerEqualOp,
        GreaterThanOp,
        GreaterEqualOp,
        ContainsOp,
        NotContainsOp,
        AddOp,
        SubOp,
        MulOp,
        DivOp,
        ModOp,
        PowerOp;
    }

    private Expr left;
    private Op op;
    private Expr right;

    public BinaryExpr(int line, Expr left, Op op, Expr right) {
        super(line);

        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public Value<?> expr() {
        Value<?> v = null;
        switch (op) {
            case AndOp:
                v = andOp();
                break;
            case OrOp:
                v = orOp();
                break;
            case EqualOp:
                v = equalOp();
                break;
            case NotEqualOp:
                v = notEqualOp();
                break;
            case LowerThanOp:
                v = lowerThanOp();
                break;
            case LowerEqualOp:
                v = lowerEqualOp();
                break;
            case GreaterThanOp:
                v = greaterThanOp();
                break;
            case GreaterEqualOp:
                v = greaterEqualOp();
                break;
            case ContainsOp:
                v = containsOp();
                break;
            case NotContainsOp:
                v = notContainsOp();
                break;
            case AddOp:
                v = addOp();
                break;
            case SubOp:
                v = subOp();
                break;
            case MulOp:
                v = mulOp();
                break;
            case DivOp:
                v = divOp();
                break;
            case ModOp:
                v = modOp();
                break;
            case PowerOp:
                v = powerOp();
                break;
            default:
                Utils.abort(super.getLine());
        }

        return v;
    }

    private Value<?> andOp() {
        return null;
    }

    private Value<?> orOp() {
        return null;
    }

    private Value<?> equalOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();
        if ((lvalue instanceof NumberValue) && !(rvalue instanceof NumberValue) ||
                ((lvalue instanceof TextValue) && !(rvalue instanceof TextValue)) ||
                ((lvalue instanceof BooleanValue) && !(rvalue instanceof BooleanValue))) {
            Utils.abort(super.getLine());
        }

        if ((lvalue instanceof NumberValue)) {
            NumberValue nlv = (NumberValue) lvalue;
            int lv = nlv.value();

            NumberValue nrv = (NumberValue) rvalue;
            int rv = nrv.value();

            if (lv == rv) {
                BooleanValue bv = new BooleanValue(true);
                return bv;
            } else {
                BooleanValue bv = new BooleanValue(false);
                return bv;
            }
        } else if ((lvalue instanceof TextValue)) {
            TextValue nlv = (TextValue) lvalue;
            String lv = nlv.value();

            TextValue nrv = (TextValue) rvalue;
            String rv = nrv.value();

            if (lv == rv) {
                BooleanValue bv = new BooleanValue(true);
                return bv;
            } else {
                BooleanValue bv = new BooleanValue(false);
                return bv;
            }
        } else {
            BooleanValue nlv = (BooleanValue) lvalue;
            Boolean lv = nlv.value();

            BooleanValue nrv = (BooleanValue) rvalue;
            Boolean rv = nrv.value();

            if (lv == rv) {
                BooleanValue bv = new BooleanValue(true);
                return bv;
            } else {
                BooleanValue bv = new BooleanValue(false);
                return bv;
            }
        }

    }

    private Value<?> notEqualOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();
        if ((lvalue instanceof NumberValue) && !(rvalue instanceof NumberValue) ||
                ((lvalue instanceof TextValue) && !(rvalue instanceof TextValue)) ||
                ((lvalue instanceof BooleanValue) && !(rvalue instanceof BooleanValue))) {
            Utils.abort(super.getLine());
        }

        if (lvalue.value() != rvalue.value()) {
            BooleanValue bv = new BooleanValue(true);
            return bv;
        } else {
            BooleanValue bv = new BooleanValue(false);
            return bv;
        }
    }

    private Value<?> lowerThanOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();
        if (!(lvalue instanceof NumberValue) || !(rvalue instanceof NumberValue)) {
            Utils.abort(super.getLine());
        }
        NumberValue nlv = (NumberValue) lvalue;
        int lv = nlv.value();

        NumberValue nrv = (NumberValue) rvalue;
        int rv = nrv.value();

        if (lv < rv) {
            BooleanValue bv = new BooleanValue(true);
            return bv;
        } else {
            BooleanValue bv = new BooleanValue(false);
            return bv;
        }
    }

    private Value<?> lowerEqualOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();
        if (!(lvalue instanceof NumberValue) || !(rvalue instanceof NumberValue)) {
            Utils.abort(super.getLine());
        }
        NumberValue nlv = (NumberValue) lvalue;
        int lv = nlv.value();

        NumberValue nrv = (NumberValue) rvalue;
        int rv = nrv.value();

        if (lv <= rv) {
            BooleanValue bv = new BooleanValue(true);
            return bv;
        } else {
            BooleanValue bv = new BooleanValue(false);
            return bv;
        }
    }

    private Value<?> greaterThanOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();
        if (!(lvalue instanceof NumberValue) || !(rvalue instanceof NumberValue)) {
            Utils.abort(super.getLine());
        }
        NumberValue nlv = (NumberValue) lvalue;
        int lv = nlv.value();

        NumberValue nrv = (NumberValue) rvalue;
        int rv = nrv.value();

        if (lv > rv) {
            BooleanValue bv = new BooleanValue(true);
            return bv;
        } else {
            BooleanValue bv = new BooleanValue(false);
            return bv;
        }
    }

    private Value<?> greaterEqualOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();
        if (!(lvalue instanceof NumberValue) || !(rvalue instanceof NumberValue)) {
            Utils.abort(super.getLine());
        }
        NumberValue nlv = (NumberValue) lvalue;
        int lv = nlv.value();

        NumberValue nrv = (NumberValue) rvalue;
        int rv = nrv.value();

        if (lv >= rv) {
            BooleanValue bv = new BooleanValue(true);
            return bv;
        } else {
            BooleanValue bv = new BooleanValue(false);
            return bv;
        }
    }

    private Value<?> containsOp() {
        return null;
    }

    private Value<?> notContainsOp() {
        return null;
    }

    private Value<?> addOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();
        Value<?> res = null;

        if ((lvalue instanceof TextValue) && (rvalue instanceof NumberValue)) {
            TextValue lv = (TextValue) lvalue;
            String slv = lv.value();

            TextValue rv = new TextValue(rvalue.toString());
            String srv = rv.value();

            TextValue ts = new TextValue(slv + srv);
            res = ts;
        } else if ((lvalue instanceof TextValue) && (lvalue instanceof TextValue)) {
            TextValue lv = (TextValue) lvalue;
            String slv = lv.value();

            TextValue rv = (TextValue) rvalue;
            String srv = rv.value();

            TextValue ts = new TextValue(slv + srv);
            res = ts;

        } else if ((lvalue instanceof NumberValue) && (rvalue instanceof TextValue)) {
            TextValue slv = new TextValue(lvalue.toString());
            String lv = slv.value();

            TextValue srv = (TextValue) rvalue;
            String rv = srv.value();

            TextValue tns = new TextValue(lv + rv);
            res = tns;
        } else if ((lvalue instanceof NumberValue) && (rvalue instanceof NumberValue)) {
            NumberValue nlv = (NumberValue) lvalue;
            int lv = nlv.value();

            NumberValue nrv = (NumberValue) rvalue;
            int rv = nrv.value();

            NumberValue n = new NumberValue(lv + rv);
            res = n;
        } else if ((lvalue == null) && (rvalue instanceof NumberValue)) {
            NumberValue nrv = (NumberValue) rvalue;
            int rv = nrv.value();

            NumberValue n = new NumberValue(rv);

            res = n;
        } else if ((lvalue == null) && (rvalue instanceof TextValue)) {

            TextValue rv = new TextValue(rvalue.toString());
            String srv = rv.value();

            TextValue ts = new TextValue("null" + srv);
            res = ts;
        } else if ((lvalue instanceof NumberValue) && (rvalue == null)) {
            NumberValue nlv = (NumberValue) rvalue;
            int lv = nlv.value();

            NumberValue n = new NumberValue(lv);

            res = n;
        } else if ((lvalue instanceof TextValue) && (rvalue == null)) {
            TextValue lv = new TextValue(rvalue.toString());
            String slv = lv.value();

            TextValue ts = new TextValue("null" + slv);
            res = ts;
        } else if (((lvalue instanceof BooleanValue) && (rvalue instanceof NumberValue)) ||
                ((lvalue instanceof BooleanValue) && (rvalue instanceof BooleanValue)) ||
                ((lvalue instanceof NumberValue) && (rvalue instanceof BooleanValue))) {

            Utils.abort(super.getLine());
        } else if ((lvalue instanceof BooleanValue) && (rvalue instanceof TextValue)) {
            TextValue vr = (TextValue) rvalue;
            String svr = vr.value();

            TextValue ts = new TextValue(lvalue.toString() + svr);
            res = ts;
        } else if ((lvalue instanceof TextValue) && (rvalue instanceof BooleanValue)) {
            TextValue svl = (TextValue) lvalue;
            String vl = svl.value();

            TextValue ts = new TextValue(vl + rvalue.toString());
            res = ts;

        } else {
            System.out.printf(this.getLine()+": Erro inesperado, linha: \n");
            System.exit(0);

        }
        return res;
    }

    private Value<?> subOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
                !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = new NumberValue(lv - rv);
        return res;
    }

    private Value<?> mulOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
                !(rvalue instanceof NumberValue)) {
            Utils.abort(super.getLine());
        }

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = new NumberValue(lv * rv);
        return res;
    }

    private Value<?> divOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
                !(rvalue instanceof NumberValue)) {
            Utils.abort(super.getLine());
        }

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = new NumberValue(lv / rv);
        return res;
    }

    private Value<?> modOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
                !(rvalue instanceof NumberValue)) {
            Utils.abort(super.getLine());
        }

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = new NumberValue(lv % rv);
        return res;
    }

    private Value<?> powerOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
                !(rvalue instanceof NumberValue)) {
            Utils.abort(super.getLine());
        }

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = new NumberValue((int) Math.pow(lv, rv));

        return res;
    }

}