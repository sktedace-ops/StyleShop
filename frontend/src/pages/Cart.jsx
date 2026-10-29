import {useEffect,useState} from "react";
import {useNavigate} from "react-router-dom";
import {request,imageUrl} from "../api";
import PageActions from "../components/PageActions";

function money(v){ return Number(v||0).toLocaleString("zh-TW"); }

export default function Cart(){
  const nav=useNavigate();
  const [cart,setCart]=useState({
    items:[], total:0, subtotal:0, shippingFee:0,
    discountAmount:0, finalAmount:0, totalQuantity:0
  });
  const [msg,setMsg]=useState("");
  const [loading,setLoading]=useState(true);

  async function load(){
    try{
      setLoading(true);
      setMsg("");
      const data=await request("/api/cart");
      const subtotal=Number(data?.subtotal ?? data?.total ?? 0);
      const shippingFee=Number(data?.shippingFee ?? 0);
      const discountAmount=Number(data?.discountAmount ?? 0);
      const finalAmount=Number(data?.finalAmount ?? data?.total ?? subtotal);
      setCart({
        items:Array.isArray(data?.items)?data.items:[],
        total:finalAmount,
        subtotal,
        shippingFee,
        discountAmount,
        finalAmount,
        totalQuantity:Number(data?.totalQuantity||0)
      });
    }catch(e){
      if(e.status===401) nav("/login");
      else setMsg(e.message||"購物車載入失敗");
    }finally{
      setLoading(false);
    }
  }

  useEffect(()=>{load()},[]);

  async function act(path,method="POST"){
    try{
      await request(path,{method});
      await load();
    }catch(e){
      setMsg(e.message||"操作失敗");
    }
  }

  async function clearCart(){
    if(!window.confirm("確定要清空購物車嗎？購物車內所有商品都會移除。")) return;
    try{
      await request("/api/cart/clear",{method:"DELETE"});
      await load();
    }catch(e){
      setMsg(e.message||"清空購物車失敗");
    }
  }

  async function checkout(){
    try{
      const o=await request("/api/orders/checkout",{method:"POST"});
      nav(`/orders/${o.id}`);
    }catch(e){
      setMsg(e.message||"建立訂單失敗");
    }
  }

  return <main><PageActions/>
    <div className="heading">
      <div>
        <h1>購物車</h1>
        <p className="muted">確認商品數量後即可建立訂單</p>
      </div>
      <span>共 {cart.totalQuantity} 件</span>
    </div>

    {msg&&<p className="error">{msg}</p>}
    {loading&&<div className="panel"><p>購物車載入中...</p></div>}

    {!loading&&cart.items.length===0&&
      <div className="panel empty-cart">
        <h2>購物車目前是空的</h2>
        <p>回到首頁選擇商品，加入購物車後會顯示在這裡。</p>
        <button className="primary" onClick={()=>nav("/")}>前往選購商品</button>
      </div>
    }

    {!loading&&cart.items.length>0&&
      <div className="checkout-layout">
        <div className="cartlist">
          {cart.items.map(i=>{
            const price=Number(i.price||0);
            const subtotal=Number(i.subtotal||0);
            return <div className="cartrow" key={i.id}>
              <img src={imageUrl(i.imageUrl)} alt={i.name||"商品"}/>
              <div className="grow">
                <h3>{i.name||"未命名商品"}</h3>
                <p className="muted">單價 NT$ {money(price)}</p>
                <strong>小計 NT$ {money(subtotal)}</strong>
              </div>
              <div className="qty">
                <button onClick={()=>act(`/api/cart/decrease/${i.id}`)}>-</button>
                <b>{Number(i.quantity||1)}</b>
                <button onClick={()=>act(`/api/cart/increase/${i.id}`)}>+</button>
                <button onClick={()=>act(`/api/cart/${i.id}`,"DELETE")}>移除</button>
              </div>
            </div>
          })}
        </div>

        <aside className="order-summary-card">
          <h2>購物車總結</h2>
          <div className="summary-line"><span>商品件數</span><b>{cart.totalQuantity} 件</b></div>
          <div className="summary-line"><span>商品小計</span><b>NT$ {money(cart.subtotal)}</b></div>
          <div className="summary-line"><span>運費</span><b>{cart.shippingFee===0?"免運":`NT$ ${money(cart.shippingFee)}`}</b></div>
          <div className="summary-line"><span>折扣</span><b>- NT$ {money(cart.discountAmount)}</b></div>
          <div className="summary-divider"/>
          <div className="summary-line summary-total"><span>訂單總額</span><b>NT$ {money(cart.finalAmount)}</b></div>
          <button className="primary summary-checkout" onClick={checkout}>建立訂單</button>
          <button className="danger-outline summary-clear" onClick={clearCart}>清空購物車</button>
          <p className="summary-note">目前專題版本設定為免運、無折扣。</p>
        </aside>
      </div>
    }
  </main>
}
