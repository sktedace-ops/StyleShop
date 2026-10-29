import {useEffect,useState} from "react";
import {useNavigate} from "react-router-dom";
import {request,imageUrl,downloadBase64Pdf} from "../api";
import PageActions from "../components/PageActions";

const empty={id:null,name:"",price:"",imageUrl:"",productUrl:"",description:"",categoryId:""};

export default function AdminProducts(){
  const nav=useNavigate();
  const [products,setProducts]=useState([]);
  const [categories,setCategories]=useState([]);
  const [form,setForm]=useState(empty);
  const [msg,setMsg]=useState("");
  const [error,setError]=useState("");
  const [loading,setLoading]=useState(true);
  const [filters,setFilters]=useState({keyword:"",categoryId:"",minPrice:"",maxPrice:"",sortBy:"id",direction:"asc"});
  const [applied,setApplied]=useState(filters);
  const [page,setPage]=useState(0);
  const [meta,setMeta]=useState({totalElements:0,totalPages:0,dao:""});
  const [importFile,setImportFile]=useState(null);
  const [importing,setImporting]=useState(false);

  async function load(){
    try{
      setLoading(true);
      setError("");
      const query=new URLSearchParams({page:String(page),size:"8",sortBy:applied.sortBy,direction:applied.direction});
      Object.entries(applied).forEach(([key,value])=>{if(value!==""&&!['sortBy','direction'].includes(key))query.set(key,value)});
      const [p,c]=await Promise.all([
        request(`/api/admin/products?${query}`),
        request("/api/categories")
      ]);
      setProducts(Array.isArray(p.content)?p.content:[]);
      setMeta({totalElements:p.totalElements||0,totalPages:p.totalPages||0,dao:p.dao||""});
      setCategories(Array.isArray(c)?c:[]);
    }catch(e){
      if(e.status===401) nav("/login");
      else setError(e.message||"商品管理資料載入失敗");
    }finally{
      setLoading(false);
    }
  }

  useEffect(()=>{load()},[page,applied]);

  function search(e){
    e.preventDefault();
    setPage(0);
    setApplied({...filters});
  }

  function resetSearch(){
    const clean={keyword:"",categoryId:"",minPrice:"",maxPrice:"",sortBy:"id",direction:"asc"};
    setFilters(clean); setPage(0); setApplied(clean);
  }

  function fileAsBase64(file){
    return new Promise((resolve,reject)=>{
      const reader=new FileReader();
      reader.onload=()=>resolve(String(reader.result||""));
      reader.onerror=()=>reject(new Error("檔案讀取失敗"));
      reader.readAsDataURL(file);
    });
  }

  async function importCsv(){
    if(!importFile){setError("請先選擇 CSV、Excel 或 PDF 檔案");return}
    setMsg("");
    setError("");
    setImporting(true);
    try{
      const base64=await fileAsBase64(importFile);
      const result=await request("/api/admin/products/import/base64",{
        method:"POST",
        body:JSON.stringify({fileName:importFile.name,base64})
      });
      setMsg(`${importFile.name} 解析 ${result.parsedCount} 筆，成功寫入 ${result.savedCount} 筆；${result.transactionStatus}（${result.dao}）`);
      setImportFile(null);
      document.getElementById("product-csv-file").value="";
      setPage(0);
      await load();
    }catch(e){
      setError(e.message||"檔案匯入失敗，整批資料已 Rollback");
    }finally{
      setImporting(false);
    }
  }

  function downloadCsvExample(){
    const csv='name,price,description,imageUrl,productUrl,categoryId\n'+
      '批次商品A,880,Base64 CSV匯入測試,/images/tshirt.svg,,1\n'+
      '批次商品B,1280,"含逗號, 也能正確解析",/images/running-shoes.svg,,2\n';
    const url=URL.createObjectURL(new Blob(["\uFEFF"+csv],{type:"text/csv;charset=utf-8"}));
    const link=document.createElement("a");
    link.href=url;
    link.download="products-import-example.csv";
    link.click();
    URL.revokeObjectURL(url);
  }

  async function exportPdf(path){
    try{setError("");await downloadBase64Pdf(path)}
    catch(e){setError(e.message||"PDF 匯出失敗")}
  }

  async function save(e){
    e.preventDefault();
    setMsg("");
    setError("");
    try{
      await request("/api/admin/products",{
        method:"POST",
        body:JSON.stringify({
          ...form,
          price:Number(form.price||0),
          categoryId:form.categoryId||null
        })
      });
      setForm(empty);
      setMsg(form.id?"商品修改成功":"商品新增成功");
      await load();
    }catch(e){
      setError(e.message||"商品儲存失敗");
    }
  }

  async function removeProduct(id){
    if(!window.confirm("確定要移除這個商品嗎？")) return;
    setMsg("");
    setError("");
    try{
      await request(`/api/admin/products/${id}`,{method:"DELETE"});
      setMsg("商品已移除");
      if(String(form.id)===String(id)) setForm(empty);
      await load();
    }catch(e){
      setError(e.message||"商品移除失敗");
    }
  }

  function editProduct(p){
    setMsg("");
    setError("");
    setForm({
      id:p.id??null,
      name:p.name??"",
      price:p.price??"",
      imageUrl:p.imageUrl??"",
      productUrl:p.productUrl??"",
      description:p.description??"",
      categoryId:p.categoryId??""
    });
    window.scrollTo({top:0,behavior:"smooth"});
  }

  return <main><PageActions/>
    <div className="heading">
      <div>
        <h1>商品管理</h1>
        <p>可切換 JPA／MyBatis DAO，支援條件查詢、Pageable 分頁與 Base64 PDF</p>
      </div>
      <span>DAO：{meta.dao||"-"}　共 {meta.totalElements} 項商品</span>
    </div>

    <div className="report-actions">
      <button className="report-button" onClick={()=>exportPdf("/api/reports/products/base64?lang=zh-TW")}>📄 商品清單（中文 PDF）</button>
      <button className="report-button secondary-report" onClick={()=>exportPdf("/api/reports/products/base64?lang=en")}>📄 Products (English PDF)</button>
      <button className="report-button" onClick={()=>exportPdf("/api/reports/login-history/base64?lang=zh-TW")}>🔐 登入紀錄（中文 PDF）</button>
      <button className="report-button secondary-report" onClick={()=>exportPdf("/api/reports/login-history/base64?lang=en")}>🔐 Login History (English PDF)</button>
      <button className="report-button" onClick={()=>exportPdf("/api/reports/orders/base64?lang=zh-TW")}>🧾 訂單紀錄（中文 PDF）</button>
      <button className="report-button secondary-report" onClick={()=>exportPdf("/api/reports/orders/base64?lang=en")}>🧾 Orders (English PDF)</button>
    </div>

    <section className="panel import-panel">
      <div>
        <h2>Base64 批次匯入商品</h2>
        <p>支援 CSV、Excel (.xlsx/.xls) 與文字型 PDF；後端解析成 Collection，全部成功才 Commit，任一列失敗即整批 Rollback。</p>
      </div>
      <input id="product-csv-file" type="file"
        accept=".csv,.xlsx,.xls,.pdf,text/csv,application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/pdf"
        onChange={e=>setImportFile(e.target.files?.[0]||null)}/>
      <button type="button" onClick={downloadCsvExample}>下載欄位範例</button>
      <button className="primary" type="button" disabled={importing} onClick={importCsv}>
        {importing?"匯入中...":"上傳並匯入"}
      </button>
    </section>

    <form className="panel search-panel" onSubmit={search}>
      <input placeholder="查詢商品名稱或描述" value={filters.keyword}
        onChange={e=>setFilters({...filters,keyword:e.target.value})}/>
      <select value={filters.categoryId} onChange={e=>setFilters({...filters,categoryId:e.target.value})}>
        <option value="">全部種類</option>
        {categories.map(c=><option key={c.id} value={c.id}>{c.name}</option>)}
      </select>
      <input type="number" min="0" placeholder="最低價格" value={filters.minPrice}
        onChange={e=>setFilters({...filters,minPrice:e.target.value})}/>
      <input type="number" min="0" placeholder="最高價格" value={filters.maxPrice}
        onChange={e=>setFilters({...filters,maxPrice:e.target.value})}/>
      <select value={filters.sortBy} onChange={e=>setFilters({...filters,sortBy:e.target.value})}>
        <option value="id">商品編號</option><option value="name">商品名稱</option><option value="price">價格</option>
      </select>
      <select value={filters.direction} onChange={e=>setFilters({...filters,direction:e.target.value})}>
        <option value="asc">由小到大</option><option value="desc">由大到小</option>
      </select>
      <button className="primary" type="submit">查詢</button>
      <button type="button" onClick={resetSearch}>清除</button>
    </form>

    {msg&&<p className="success-msg">{msg}</p>}
    {error&&<p className="error">{error}</p>}

    <div className="adminlayout">
      <form className="panel admin-form" onSubmit={save}>
        <h2>{form.id?"修改商品":"登記商品"}</h2>

        <label>商品名稱
          <input required value={form.name}
            onChange={e=>setForm({...form,name:e.target.value})}/>
        </label>

        <label>價格
          <input required min="0" type="number" value={form.price}
            onChange={e=>setForm({...form,price:e.target.value})}/>
        </label>

        <label>種類
          <select value={form.categoryId??""}
            onChange={e=>setForm({...form,categoryId:e.target.value})}>
            <option value="">未分類</option>
            {categories.map(c=><option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
        </label>

        <label>圖片路徑
          <input placeholder="/images/example.svg" value={form.imageUrl??""}
            onChange={e=>setForm({...form,imageUrl:e.target.value})}/>
        </label>

        <label>商品網址
          <input value={form.productUrl??""}
            onChange={e=>setForm({...form,productUrl:e.target.value})}/>
        </label>

        <label>商品描述
          <textarea value={form.description??""}
            onChange={e=>setForm({...form,description:e.target.value})}/>
        </label>

        <button className="primary" type="submit">{form.id?"儲存修改":"新增商品"}</button>
        {form.id&&<button type="button" onClick={()=>setForm(empty)}>取消修改</button>}
      </form>

      <section>
        {loading&&<div className="panel"><p>商品資料載入中...</p></div>}

        {!loading&&!error&&products.length===0&&
          <div className="panel empty-admin">
            <h2>目前沒有商品</h2>
            <p>可以使用左側的「登記商品」表單新增第一項商品。</p>
          </div>
        }

        {!loading&&products.length>0&&
          <><div className="adminproducts">
            {products.map(p=>{
              const price=Number(p.price||0);
              return <div className="adminproduct" key={p.id}>
                <img src={imageUrl(p.imageUrl)} alt={p.name||"商品"}/>
                <div className="grow">
                  <b>{p.name||"未命名商品"}</b>
                  <p>{p.categoryName||"未分類"} · NT$ {price.toLocaleString()}</p>
                  <small>商品編號：{p.id}</small>
                </div>
                <button onClick={()=>editProduct(p)}>修改</button>
                <button onClick={()=>removeProduct(p.id)}>移除</button>
              </div>
            })}
          </div>
          <nav className="pagination" aria-label="商品換頁">
            <button disabled={page===0} onClick={()=>setPage(p=>p-1)}>上一頁</button>
            {Array.from({length:meta.totalPages},(_,i)=>i).map(i=><button key={i}
              className={i===page?"active":""} onClick={()=>setPage(i)}>{i+1}</button>)}
            <button disabled={page+1>=meta.totalPages} onClick={()=>setPage(p=>p+1)}>下一頁</button>
          </nav></>
        }
      </section>
    </div>
  </main>;
}
