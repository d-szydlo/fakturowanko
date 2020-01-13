package com.fakturowanko;

import com.fakturowanko.db.HibernateUtil;
import com.fakturowanko.db.KlientEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * klasa obslugujaca baze danych
 * @author dszyd
 *
 */
//TODO docelowo tej klasy ma nie być w ogole
public class DataExpert {

    protected ArrayList<Client> clientList;
    protected ArrayList<Product> productList;
    protected ArrayList<Invoice> invoiceList;

    DataExpert(){
        clientList = new ArrayList<>();
        productList = new ArrayList<>();
        invoiceList = new ArrayList<>();
//        addClient(1, "Dominika Szydlo", "ul.Piastowska 34/4", "Wroclaw", "50-361","022899");
        addProduct(1, "Pad thai", 22.0);
        addProduct(2, "Krewetki", 34.50);
        addProduct(3, "Hummus", 15.0);
        addProduct(4, "Woda 0.5L", 3.90);
        addProduct(5, "Hopium Ale", 9.80);
    }

    protected int addClient(MainFrame frame, String name, String adress, String city, String postalC, String nip) {
        KlientEntity client;
        if (name.equals("") || adress.equals("") || city.equals("") || postalC.equals("")) {
            client = new KlientEntity(null, null, null, null, null);
        } else if (nip.equals("")){
            client = new KlientEntity(name, null, city, postalC,adress);
        } else {
            client = new KlientEntity(name, nip, city, postalC,adress);
        }

        int actualId = 0;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            session.beginTransaction();
            session.save(client);
            session.getTransaction().commit();

            String hql = "SELECT MAX(Klient.idKlienta) FROM KlientEntity Klient";
            Query hqlQuery = session.createQuery(hql);
            List results = hqlQuery.list();
            actualId = (int)results.get(0);

        } catch (Exception e) {
            HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
            JOptionPane.showMessageDialog(frame, "Bad Input data", "Achtung!!!", JOptionPane.WARNING_MESSAGE);
            e.printStackTrace();
        }

        return actualId;
    }

    protected void addProduct(int id, String name, double price){
        Product product = new Product(id, name, price);
        productList.add(product);
    }

    protected void addInvoice(Invoice invoice){
        invoiceList.add(invoice);
    }

    protected int getNewClientId() {
        return clientList.size()+1;
    }

    protected int getNewProductId() {
        return productList.size()+1;
    }

    protected int getNewInvoiceId() {
        return invoiceList.size()+1;
    }

    protected Product getProduct(String name) {
        for (int i=0;i<productList.size();i++) {
            if(productList.get(i).getName().equals(name)) {
                return productList.get(i);
            }
        }
        return null;
    }

    public boolean clientChecker(int clientId) {
        List<Long> results = new ArrayList<>();
        results.add((long)0);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(*) FROM KlientEntity Klient WHERE idKlienta = ?1";
            Query hqlQuery = session.createQuery(hql);
            results = hqlQuery.setParameter(1, clientId).list();
            System.out.println(results.get(0));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return results.get(0).equals((long)1);
    }

    public KlientEntity getClient(int index) {
        List<KlientEntity> results = new ArrayList();
        results.add(null);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM KlientEntity Klient WHERE Klient.idKlienta = " + index;
            Query hqlQuery = session.createQuery(hql);
            results = hqlQuery.list();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return results.get(0);
    }

    public List getProductList() {
        List<ProductQuantity> results = new ArrayList();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM ProduktEntity Products WHERE Products.sprzedawany = " + 1;
            Query hqlQuery = session.createQuery(hql);
            results = hqlQuery.list();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

    protected String getProductName(int index) {
        for(int i=0;i<productList.size();i++) {
            if(productList.get(i).getProductId()==index) return productList.get(i).getName();
        }
        return null;
    }

    protected Invoice getInvoice(int index) {
        for (int i=0;i<invoiceList.size();i++) {
            if(invoiceList.get(i).getInvoiceId()==index) return invoiceList.get(i);
        }
        return null;
    }

    protected double getProductPrice(int index) {
        for (int i=0;i<productList.size();i++) {
            if(productList.get(i).getProductId()==index) return productList.get(i).getPrice();
        }
        return 0.0;
    }
}
